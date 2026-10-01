package com.university.learningmanagementsystem.dto.student;

import java.util.Set;

public record StudentDto(
        Long id,
        String firstName,
        String lastName,
        Set<Long> groupIds
) {
}
