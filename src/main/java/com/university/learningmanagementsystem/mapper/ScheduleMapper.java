package com.university.learningmanagementsystem.mapper;

import com.university.learningmanagementsystem.dto.schedule.ScheduleCreateDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleUpdateDto;
import com.university.learningmanagementsystem.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "courseId", source = "course.id")
    ScheduleDto toDto(Schedule schedule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Schedule toEntity(ScheduleCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void update(ScheduleUpdateDto dto, @MappingTarget Schedule schedule);
}