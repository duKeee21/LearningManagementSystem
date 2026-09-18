package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherUpdateDto;
import com.university.learningmanagementsystem.entity.Teacher;
import com.university.learningmanagementsystem.mapper.TeacherMapper;
import com.university.learningmanagementsystem.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    @Transactional(readOnly = true)
    public Page<TeacherDto> findAll(Pageable pageable) {
        return teacherRepository.findAll(pageable).map(teacherMapper::toDto);
    }

    @Transactional(readOnly = true)
    public TeacherDto findById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Учителя с id: " + id + " не существует!"));
        return teacherMapper.toDto(teacher);
    }

    @Transactional
    public TeacherDto create(TeacherCreateDto request) {
        Teacher teacher = teacherMapper.toEntity(request);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toDto(savedTeacher);
    }

    @Transactional
    public void delete(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Учителя с id: " + id + " не существует!"));

        teacher.setDeleted(true);
    }

    @Transactional
    public TeacherDto update(Long id, TeacherUpdateDto request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Учителя с id: " + id + " не существует!"));
        teacherMapper.update(request, teacher);
        return teacherMapper.toDto(teacher);
    }
}
