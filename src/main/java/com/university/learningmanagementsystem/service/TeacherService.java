package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherFilter;
import com.university.learningmanagementsystem.dto.teacher.TeacherUpdateDto;
import com.university.learningmanagementsystem.entity.Teacher;
import com.university.learningmanagementsystem.mapper.TeacherMapper;
import com.university.learningmanagementsystem.repository.TeacherRepository;
import com.university.learningmanagementsystem.specification.TeacherSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    @Transactional(readOnly = true)
    public PageResponse<TeacherDto> findAll(TeacherFilter filter, Pageable pageable) {
        Specification<Teacher> specification = TeacherSpecifications.byFilter(filter);
        Page<TeacherDto> page = teacherRepository.findAll(specification, pageable).map(teacherMapper::toDto);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public TeacherDto findById(Long id) {
        return teacherMapper.toDto(teacherRepository.requireById(id));
    }

    @Transactional
    public TeacherDto create(TeacherCreateDto request) {
        Teacher teacher = teacherMapper.toEntity(request);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toDto(savedTeacher);
    }

    @Transactional
    public void delete(Long id) {
        Teacher teacher = teacherRepository.requireById(id);
        teacher.setDeleted(true);
    }

    @Transactional
    public TeacherDto update(Long id, TeacherUpdateDto request) {
        Teacher teacher = teacherRepository.requireById(id);

        teacherMapper.mapInto(request, teacher);
        return teacherMapper.toDto(teacher);
    }
}
