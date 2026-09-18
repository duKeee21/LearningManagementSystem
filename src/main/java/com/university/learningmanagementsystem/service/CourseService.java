package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.course.CourseCreateDto;
import com.university.learningmanagementsystem.dto.course.CourseDto;
import com.university.learningmanagementsystem.dto.course.CourseUpdateDto;
import com.university.learningmanagementsystem.entity.Course;
import com.university.learningmanagementsystem.entity.Teacher;
import com.university.learningmanagementsystem.mapper.CourseMapper;
import com.university.learningmanagementsystem.repository.CourseRepository;
import com.university.learningmanagementsystem.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseMapper courseMapper;

    @Transactional(readOnly = true)
    public Page<CourseDto> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable).map(courseMapper::toDto);
    }

    @Transactional(readOnly = true)
    public CourseDto findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Курса с id: " + id + " не существует!"));
        return courseMapper.toDto(course);
    }

    @Transactional
    public CourseDto create(CourseCreateDto request) {
        Course course = courseMapper.toEntity(request);
        course.setTeacher(requestTeacher(request.teacherId()));
        Course saved = courseRepository.save(course);
        return courseMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Курса с id: " + id + " не существует!"));
        course.setDeleted(true);
    }

    @Transactional
    public CourseDto update(Long id, CourseUpdateDto request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Курса с id: " + id + " не существует!"));

        courseMapper.update(request, course);
        course.setTeacher(requestTeacher(request.teacherId()));

        return courseMapper.toDto(course);
    }

    private Teacher requestTeacher(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Учителя с id: " + teacherId + " не существует!"));
    }
}