package ru.autopark.autopark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Введите марку автомобиля")
    @Pattern(
            regexp = "^[А-Яа-яЁё0-9\\s.,«»()'’&-]+$",
            message = "Марка автомобиля должна быть написана кириллицей"
    )
    @Column(nullable = false)
    private String brand;

    @NotNull(message = "Укажите год выпуска")
    private Integer year;

    @NotNull(message = "Укажите дату постановки на учёт")
    private LocalDate registrationDate;

    @NotBlank(message = "Введите ФИО владельца")
    @Column(nullable = false)
    private String ownerFullName;

    public Car() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getOwnerFullName() {
        return ownerFullName;
    }

    public void setOwnerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
    }
}