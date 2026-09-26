package com.university.learningmanagementsystem.dto.student;

public record StudentFilter(
        String firstName,
        String lastName,
        Long groupId
) {
}
