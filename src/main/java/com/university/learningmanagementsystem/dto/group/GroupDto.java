package com.university.learningmanagementsystem.dto.group;

import java.util.Set;

public record GroupDto(
        Long id,
        String name,
        Set<Long> studentIds
) {
}
