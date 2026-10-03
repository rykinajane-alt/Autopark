
package ru.autopark.autopark.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.autopark.autopark.entity.Car;
import ru.autopark.autopark.service.CarService;

@Controller
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    // Список автомобилей
    @GetMapping
    public String getAllCars(Model model) {
        model.addAttribute("cars", carService.getAllCars());
        return "cars";
    }

    // Форма добавления автомобиля
    @GetMapping("/new")
    public String addCarForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("pageTitle", "Добавление автомобиля");
        return "car-form";
    }

    // Форма редактирования автомобиля
    @GetMapping("/edit/{id}")
    public String editCarForm(@PathVariable Long id, Model model) {
        model.addAttribute("car", carService.getCarById(id));
        model.addAttribute("pageTitle", "Редактирование автомобиля");
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
            model.addAttribute("pageTitle",
                    car.getId() == null
                            ? "Добавление автомобиля"
                            : "Редактирование автомобиля");
            return "car-form";
        }

        carService.saveCar(car);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Автомобиль успешно сохранён");

        return "redirect:/cars";
    }

    // Удаление автомобиля
    @PostMapping("/delete/{id}")
    public String deleteCar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        carService.deleteCar(id);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Автомобиль успешно удалён");

        return "redirect:/cars";
    }
}
