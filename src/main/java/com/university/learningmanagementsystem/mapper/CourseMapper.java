package com.university.learningmanagementsystem.mapper;

import com.university.learningmanagementsystem.dto.course.CourseCreateDto;
import com.university.learningmanagementsystem.dto.course.CourseDto;
import com.university.learningmanagementsystem.dto.course.CourseUpdateDto;
import com.university.learningmanagementsystem.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CourseMapper {

    @Mapping(target = "teacherId", source = "teacher.id")
    CourseDto toDto(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Course toEntity(CourseCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void mapInto(CourseUpdateDto dto, @MappingTarget Course course);
}