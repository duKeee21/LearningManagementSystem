package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.group.GroupUpdateDto;
import com.university.learningmanagementsystem.entity.Group;
import com.university.learningmanagementsystem.entity.Student;
import com.university.learningmanagementsystem.mapper.GroupMapper;
import com.university.learningmanagementsystem.repository.GroupRepository;
import com.university.learningmanagementsystem.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public PageResponse<GroupDto> findAll(Pageable pageable) {
        Page<GroupDto> page = groupRepository.findAll(pageable).map(groupMapper::toDto);
        return PageResponse.from(page);
    }


    @Transactional(readOnly = true)
    public GroupDto findById(Long id) {
        return groupMapper.toDto(groupRepository.requireById(id));
    }

    @Transactional
    public GroupDto create(GroupCreateDto request) {
        Group group = groupMapper.toEntity(request);
        addStudents(group, request.studentIds());
        Group savedGroup = groupRepository.save(group);
        return groupMapper.toDto(savedGroup);
    }

    @Transactional
    public void delete(Long id) {
        Group group = groupRepository.requireById(id);
        group.setDeleted(true);
    }

    @Transactional
    public GroupDto update(Long id, GroupUpdateDto request) {
        Group group = groupRepository.requireById(id);

        groupMapper.mapInto(request, group);

        Set<Student> oldStudents = new HashSet<>(group.getStudents());
        for (Student student : oldStudents) {
            group.getStudents().remove(student);
            student.getGroups().remove(group);
        }
        addStudents(group, request.studentIds());
        return groupMapper.toDto(group);
    }

    private void addStudents(Group group, Set<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return;
        }
        Set<Student> students = studentRepository.findAllByIdIn(studentIds);
        if (students.size() != studentIds.size()) {
            throw new EntityNotFoundException("Студенты не найдены: " + studentIds);
        }
        for (Student student : students) {
            group.getStudents().add(student);
            student.getGroups().add(group);
        }

    }
}