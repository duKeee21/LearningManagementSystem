package com.university.learningmanagementsystem.dto.course;

public record CourseDto(
        Long id,
        String name,
        String description,
        Long teacherId
) {
}