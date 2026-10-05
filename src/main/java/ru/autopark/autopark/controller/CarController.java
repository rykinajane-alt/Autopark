package ru.autopark.autopark.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.autopark.autopark.dto.DailyCarStat;
import ru.autopark.autopark.entity.Car;
import ru.autopark.autopark.service.CarService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    // Список автомобилей, поиск и сортировка
    @GetMapping
    public String getAllCars(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        List<Car> cars = new ArrayList<>(carService.searchCars(keyword));

        // Сортировка сначала по году выпуска,
        // затем по марке автомобиля
        Comparator<Car> comparator = Comparator
                .comparing(
                        Car::getYear,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                )
                .thenComparing(
                        Car::getBrand,
                        Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER)
                );

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        cars.sort(comparator);

        model.addAttribute("cars", cars);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortDir", sortDir);

        return "cars";
    }

    // Статистика за последние 30 дней
    @GetMapping("/statistics")
    public String getStatistics(Model model) {

        List<Car> cars = carService.getAllCars();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(29);

        // Подсчёт автомобилей по датам постановки на учёт
        Map<LocalDate, Long> countsByDate = cars.stream()
                .filter(car -> car.getRegistrationDate() != null)
                .filter(car -> {
                    LocalDate date = car.getRegistrationDate();

                    return !date.isBefore(startDate)
                            && !date.isAfter(today);
                })
                .collect(Collectors.groupingBy(
                        Car::getRegistrationDate,
                        Collectors.counting()
                ));

        // Максимальное количество автомобилей за один день
        long maxCount = countsByDate.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM");

        // Формируем данные для всех 30 дней,
        // включая дни без автомобилей
        List<DailyCarStat> dailyStats =
                java.util.stream.IntStream.range(0, 30)
                        .mapToObj(i -> {

                            LocalDate date = startDate.plusDays(i);

                            long count = countsByDate.getOrDefault(
                                    date,
                                    0L
                            );

                            // Высота столбца в процентах
                            int percent = maxCount == 0
                                    ? 0
                                    : (int) Math.round(
                                    count * 100.0 / maxCount
                            );

                            return new DailyCarStat(
                                    date.format(formatter),
                                    count,
                                    percent
                            );
                        })
                        .toList();

        // Общее количество автомобилей
        // за последние 30 дней
        long totalInPeriod = countsByDate.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        model.addAttribute("dailyStats", dailyStats);
        model.addAttribute("totalInPeriod", totalInPeriod);
        model.addAttribute("startDate", startDate);
        model.addAttribute("today", today);

        return "statistics";
    }

    // Форма добавления автомобиля
    @GetMapping("/new")
    public String addCarForm(Model model) {

        model.addAttribute("car", new Car());
        model.addAttribute(
                "pageTitle",
                "Добавление автомобиля"
        );

        return "car-form";
    }

    // Форма редактирования автомобиля
    @GetMapping("/edit/{id}")
    public String editCarForm(
            @PathVariable Long id,
            Model model) {

        Car car = carService.getCarById(id);

        // Проверка получения даты из базы данных
        System.out.println(
                "Дата постановки на учёт: "
                        + car.getRegistrationDate()
        );

        model.addAttribute("car", car);
        model.addAttribute(
                "pageTitle",
                "Редактирование автомобиля"
        );

        return "car-form";
    }

    // Сохранение автомобиля
    @PostMapping("/save")
    public String saveCar(
            @Valid @ModelAttribute("car") Car car,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            model.addAttribute(
                    "pageTitle",
                    car.getId() == null
                            ? "Добавление автомобиля"
                            : "Редактирование автомобиля"
            );

            return "car-form";
        }

        carService.saveCar(car);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Автомобиль успешно сохранён"
        );

        return "redirect:/cars";
    }

    // Удаление автомобиля
    @PostMapping("/delete/{id}")
    public String deleteCar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        carService.deleteCar(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Автомобиль успешно удалён"
        );

        return "redirect:/cars";
    }
}