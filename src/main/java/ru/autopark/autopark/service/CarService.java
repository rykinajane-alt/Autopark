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

    // Получение всех автомобилей
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // Поиск автомобилей по марке
    public List<Car> searchCars(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return carRepository.findAll();
        }

        return carRepository.findByBrandContainingIgnoreCase(keyword.trim());
    }

    // Получение автомобиля по ID
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
    }

    // Сохранение автомобиля
    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    // Удаление автомобиля
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}