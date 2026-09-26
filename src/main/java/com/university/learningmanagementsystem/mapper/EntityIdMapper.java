package com.university.learningmanagementsystem.mapper;

import com.university.learningmanagementsystem.entity.Group;
import com.university.learningmanagementsystem.entity.Student;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityIdMapper {

    public Set<Long> groupIds(Set<Group> groups) {
        if (groups == null) return null;
        return groups.stream().map(Group::getId).collect(Collectors.toSet());
    }

    public Set<Long> studentIds(Set<Student> students) {
        if (students == null) return null;
        return students.stream().map(Student::getId).collect(Collectors.toSet());
    }
}