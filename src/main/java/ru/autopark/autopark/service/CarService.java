package ru.autopark.autopark.service;

import org.springframework.stereotype.Service;
import ru.autopark.autopark.entity.Car;
import ru.autopark.autopark.repository.CarRepository;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    // Получить все автомобили
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // Поиск по марке, модели или регистрационному номеру
    public List<Car> searchCars(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCars();
        }

        String search = keyword.trim();

        return carRepository
                .findByBrandContainingIgnoreCaseOrModelContainingIgnoreCaseOrRegistrationNumberContainingIgnoreCase(
                        search,
                        search,
                        search
                );
    }

    // Получить автомобиль по ID
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
    }

    // Сохранить автомобиль
    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    // Удалить автомобиль
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}