package com.university.learningmanagementsystem.dto.schedule;

import java.time.LocalDateTime;

public record ScheduleDto(
        Long id,
        Long groupId,
        Long teacherId,
        Long courseId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}