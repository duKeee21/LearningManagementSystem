package com.university.learningmanagementsystem.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record StudentCreateDto(

        @NotBlank(message = "Имя обязательно для заполнения")
        String firstName,

        @NotBlank(message = "Фамилия обязательна для заполнения")
        String lastName,

        @NotEmpty(message = "Студент должен состоять минимум в 1 группе")
        Set<Long> groupIds
) {
}
