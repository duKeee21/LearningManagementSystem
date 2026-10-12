package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.config.IntegrationTestConfig;
import com.university.learningmanagementsystem.dto.course.CourseCreateDto;
import com.university.learningmanagementsystem.dto.course.CourseDto;
import com.university.learningmanagementsystem.dto.course.CourseUpdateDto;
import com.university.learningmanagementsystem.entity.Teacher;
import com.university.learningmanagementsystem.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourseServiceTest extends IntegrationTestConfig {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher createTeacher() {
        return teacherRepository.save(
                Teacher.builder().firstName("Иван").lastName("Иванов").build());
    }

    @Test
    void shouldCreateCourse() {
        Teacher teacher = createTeacher();

        CourseDto saved = courseService.create(
                new CourseCreateDto("Математика", "Описание", teacher.getId()));

        assertThat(saved.id()).isNotNull();
        assertThat(saved.name()).isEqualTo("Математика");
        assertThat(saved.teacherId()).isEqualTo(teacher.getId());
    }

    @Test
    void shouldThrowWhenTeacherNotFound() {
        assertThatThrownBy(() -> courseService.create(
                new CourseCreateDto("Математика", "Описание", 99999L)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldUpdateCourse() {
        Teacher teacher = createTeacher();
        CourseDto created = courseService.create(
                new CourseCreateDto("Математика", "Описание", teacher.getId()));

        CourseDto updated = courseService.update(created.id(),
                new CourseUpdateDto("Физика", "Описание", teacher.getId()));

        assertThat(updated.name()).isEqualTo("Физика");
    }

    @Test
    void shouldDeleteCourse() {
        Teacher teacher = createTeacher();
        CourseDto created = courseService.create(
                new CourseCreateDto("Математика", "Описание", teacher.getId()));

        courseService.delete(created.id());

        assertThatThrownBy(() -> courseService.findById(created.id()))
                .isInstanceOf(RuntimeException.class);
    }
}