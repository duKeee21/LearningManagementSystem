package com.university.learningmanagementsystem.mapper;

import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.group.GroupUpdateDto;
import com.university.learningmanagementsystem.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = EntityIdMapper.class)
public interface GroupMapper {
    @Mapping(target = "studentIds", source = "students")
    GroupDto toDto(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Group toEntity(GroupCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void update(GroupUpdateDto dto, @MappingTarget Group group);

}
