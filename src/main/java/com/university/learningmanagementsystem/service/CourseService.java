package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.course.CourseCreateDto;
import com.university.learningmanagementsystem.dto.course.CourseDto;
import com.university.learningmanagementsystem.dto.course.CourseUpdateDto;
import com.university.learningmanagementsystem.entity.Course;
import com.university.learningmanagementsystem.mapper.CourseMapper;
import com.university.learningmanagementsystem.repository.CourseRepository;
import com.university.learningmanagementsystem.repository.TeacherRepository;
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
    public PageResponse<CourseDto> findAll(Pageable pageable) {
        Page<CourseDto> page = courseRepository.findAll(pageable).map(courseMapper::toDto);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public CourseDto findById(Long id) {
        return courseMapper.toDto(courseRepository.requireById(id));
    }

    @Transactional
    public CourseDto create(CourseCreateDto request) {
        Course course = courseMapper.toEntity(request);
        course.setTeacher(teacherRepository.requireById(request.teacherId()));
        Course saved = courseRepository.save(course);
        return courseMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.requireById(id);
        course.setDeleted(true);
    }

    @Transactional
    public CourseDto update(Long id, CourseUpdateDto request) {
        Course course = courseRepository.requireById(id);

        courseMapper.mapInto(request, course);
        course.setTeacher(teacherRepository.requireById(request.teacherId()));

        return courseMapper.toDto(course);
    }
}