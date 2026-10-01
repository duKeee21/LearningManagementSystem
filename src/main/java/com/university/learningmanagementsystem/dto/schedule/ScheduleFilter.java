package com.university.learningmanagementsystem.dto.schedule;

public record ScheduleFilter(
        Long groupId,
        Long teacherId,
        Long courseId
) {
}
