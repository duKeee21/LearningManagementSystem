package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherUpdateDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class TeacherServiceTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM teachers");
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("DELETE FROM teachers");
    }

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