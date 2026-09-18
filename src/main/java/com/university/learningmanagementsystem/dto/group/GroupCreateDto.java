package com.university.learningmanagementsystem.dto.group;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record GroupCreateDto(
        @NotBlank(message = "Название группы обязательно!")
        String name,

        Set<Long> studentIds
) {
}
