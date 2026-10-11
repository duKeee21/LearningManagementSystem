package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.config.IntegrationTestConfig;
import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeacherServiceTest extends IntegrationTestConfig {

    @Autowired
    private TeacherService teacherService;

    @Test
    void shouldCreateTeacher() {
        TeacherDto saved = teacherService.create(new TeacherCreateDto("Иван", "Иванов"));
        assertThat(saved.id()).isNotNull();
        assertThat(saved.firstName()).isEqualTo("Иван");
        assertThat(saved.lastName()).isEqualTo("Иванов");
    }

    @Test
    void shouldThrowWhenTeacherNotFound() {
        assertThatThrownBy(() -> teacherService.findById(100000L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldUpdateTeacher() {
        TeacherDto created = teacherService.create(new TeacherCreateDto("Иван", "Иванов"));
        TeacherDto updated = teacherService.update(created.id(),
                new TeacherUpdateDto("Пётр", "Петров"));
        assertThat(updated.firstName()).isEqualTo("Пётр");
        assertThat(updated.lastName()).isEqualTo("Петров");
    }

    @Test
    void shouldDeleteTeacher() {
        TeacherDto created = teacherService.create(new TeacherCreateDto("Иван", "Иванов"));
        teacherService.delete(created.id());
        assertThatThrownBy(() -> teacherService.findById(created.id()))
                .isInstanceOf(RuntimeException.class);
    }
}