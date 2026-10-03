package ru.autopark.autopark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.autopark.autopark.entity.Car;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByBrandContainingIgnoreCase(String brand);
}