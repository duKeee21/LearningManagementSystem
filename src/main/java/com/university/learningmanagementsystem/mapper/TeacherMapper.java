package com.university.learningmanagementsystem.mapper;

import com.university.learningmanagementsystem.dto.Teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.Teacher.TeacherDto;
import com.university.learningmanagementsystem.dto.Teacher.TeacherUpdateDto;
import com.university.learningmanagementsystem.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    TeacherDto toDto(Teacher teacher);

    Teacher toEntity(TeacherCreateDto dto);

    void update(TeacherUpdateDto dto, @MappingTarget Teacher teacher);
}
