package com.model;

import java.time.LocalDate;

public record Person(Long id, String firstName, String lastName, int age, LocalDate registrationDate) {
}
