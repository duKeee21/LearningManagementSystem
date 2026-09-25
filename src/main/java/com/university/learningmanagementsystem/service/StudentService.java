package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.student.StudentCreateDto;
import com.university.learningmanagementsystem.dto.student.StudentDto;
import com.university.learningmanagementsystem.dto.student.StudentUpdateDto;
import com.university.learningmanagementsystem.entity.Group;
import com.university.learningmanagementsystem.entity.Student;
import com.university.learningmanagementsystem.mapper.StudentMapper;
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
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public PageResponse<StudentDto> findAll(Pageable pageable) {
        Page<StudentDto> page = studentRepository.findAll(pageable).map(studentMapper::toDto);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public StudentDto findById(Long id) {
        return studentMapper.toDto(studentRepository.requireById(id));
    }

    @Transactional
    public StudentDto create(StudentCreateDto request) {
        Student student = studentMapper.toEntity(request);
        addGroups(student, request.groupIds());
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }

    @Transactional
    public void delete(Long id) {
        Student student = studentRepository.requireById(id);
        student.setDeleted(true);
    }

    @Transactional
    public StudentDto update(Long id, StudentUpdateDto request) {
        Student student = studentRepository.requireById(id);

        studentMapper.mapInto(request, student);

        Set<Group> oldGroups = new HashSet<>(student.getGroups());
        for (Group group : oldGroups) {
            student.getGroups().remove(group);
            group.getStudents().remove(student);
        }
        addGroups(student, request.groupIds());
        return studentMapper.toDto(student);
    }

    private void addGroups(Student student, Set<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            throw new IllegalArgumentException("Студент должен состоять минимум в 1 группе");
        }
        Set<Group> groups = groupRepository.findAllByIdIn(groupIds);
        if (groups.size() != groupIds.size()) {
            throw new EntityNotFoundException("Группы не найдены");
        }
        for (Group group : groups) {
            student.getGroups().add(group);
            group.getStudents().add(student);
        }
    }
}