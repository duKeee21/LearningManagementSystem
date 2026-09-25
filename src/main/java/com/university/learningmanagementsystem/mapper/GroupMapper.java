package com.university.learningmanagementsystem.mapper;

import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.group.GroupUpdateDto;
import com.university.learningmanagementsystem.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = EntityIdMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface GroupMapper {
    @Mapping(target = "studentIds", source = "students")
    GroupDto toDto(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Group toEntity(GroupCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void mapInto(GroupUpdateDto dto, @MappingTarget Group group);

}
