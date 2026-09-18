package com.university.learningmanagementsystem.service;

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
    public Page<StudentDto> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable).map(studentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public StudentDto findById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студента с id: " + id + " не существует!"));
        return studentMapper.toDto(student);
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
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студента с id: " + id + " не существует!"));

        student.setDeleted(true);
    }

    @Transactional
    public StudentDto update(Long id, StudentUpdateDto request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студента с id: " + id + " не существует!"));
        studentMapper.update(request, student);

        new HashSet<>(student.getGroups()).forEach(student::removeGroup);
        addGroups(student, request.groupIds());
        return studentMapper.toDto(student);
    }

    private void addGroups(Student student, Set<Long> groupIds) {
        Set<Group> groups = new HashSet<>(groupRepository.findAllById(groupIds));
        if (groups.size() != groupIds.size()) {
            throw new EntityNotFoundException("Группы не найдены");
        }
        groups.forEach(student::addGroup);
    }
}
