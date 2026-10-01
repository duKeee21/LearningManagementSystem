package com.university.learningmanagementsystem.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseUpdateDto(

        @NotBlank(message = "Название курса обязательно для заполнения")
        String name,

        String description,

        @NotNull(message = "ID учителя обязателен")
        Long teacherId
) {
}