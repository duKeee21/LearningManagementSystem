package com.university.learningmanagementsystem.dto.teacher;

import jakarta.validation.constraints.NotBlank;

public record TeacherUpdateDto(

        @NotBlank(message = "Имя обязательно для заполнения")
        String firstName,

        @NotBlank(message = "Фамилия обязательна для заполнения")
        String lastName
) {
}
