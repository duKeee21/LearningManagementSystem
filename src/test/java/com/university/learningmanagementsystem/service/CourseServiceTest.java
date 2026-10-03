package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.course.CourseCreateDto;
import com.university.learningmanagementsystem.dto.course.CourseDto;
import com.university.learningmanagementsystem.entity.Teacher;
import com.university.learningmanagementsystem.repository.TeacherRepository;
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
class CourseServiceTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM courses");
        jdbcTemplate.execute("DELETE FROM teachers");
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("DELETE FROM courses");
        jdbcTemplate.execute("DELETE FROM teachers");
    }

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
                new com.university.learningmanagementsystem.dto.course.CourseUpdateDto(
                        "Физика", "Описание", teacher.getId()));

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