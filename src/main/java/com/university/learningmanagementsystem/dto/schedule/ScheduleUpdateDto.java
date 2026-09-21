package com.university.learningmanagementsystem.dto.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleUpdateDto(

        @NotNull(message = "ID группы обязателен")
        Long groupId,

        @NotNull(message = "ID учителя обязателен")
        Long teacherId,

        @NotNull(message = "ID курса обязателен")
        Long courseId,

        @NotNull(message = "Дата начала обязательна")
        LocalDateTime startDate,

        @NotNull(message = "Дата окончания обязательна")
        LocalDateTime endDate
) {
}