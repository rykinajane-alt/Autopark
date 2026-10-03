package ru.autopark.autopark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.autopark.autopark.entity.Car;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Car> findByBrandContainingIgnoreCaseOrModelContainingIgnoreCaseOrRegistrationNumberContainingIgnoreCase(
            String brand,
            String model,
            String registrationNumber
    );
}