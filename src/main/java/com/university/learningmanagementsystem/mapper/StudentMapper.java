package com.university.learningmanagementsystem.mapper;

import com.university.learningmanagementsystem.dto.student.StudentCreateDto;
import com.university.learningmanagementsystem.dto.student.StudentDto;
import com.university.learningmanagementsystem.dto.student.StudentUpdateDto;
import com.university.learningmanagementsystem.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = EntityIdMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface StudentMapper {

    @Mapping(target = "groupIds", source = "groups")
    StudentDto toDto(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toEntity(StudentCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void mapInto(StudentUpdateDto dto, @MappingTarget Student student);
}
