package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.TestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.course.CourseCreateDto;
import com.university.learningmanagementsystem.dto.course.CourseDto;
import com.university.learningmanagementsystem.dto.course.CourseUpdateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestConfig.class)
class CourseControllerTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        cleanDatabase();
    }

    @AfterEach
    void cleanUp() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM schedule");
        jdbcTemplate.execute("DELETE FROM student_group");
        jdbcTemplate.execute("DELETE FROM students");
        jdbcTemplate.execute("DELETE FROM courses");
        jdbcTemplate.execute("DELETE FROM groups");
        jdbcTemplate.execute("DELETE FROM teachers");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private TeacherDto createTeacher(String firstName, String lastName) {
        TeacherCreateDto request = new TeacherCreateDto(firstName, lastName);
        return restTemplate.postForEntity(
                url("/api/v1/teachers"), request, TeacherDto.class).getBody();
    }

    private CourseDto createCourse(String name, Long teacherId) {
        CourseCreateDto request = new CourseCreateDto(name, "Описание", teacherId);
        return restTemplate.postForEntity(
                url("/api/v1/courses"), request, CourseDto.class).getBody();
    }

    @Test
    void shouldCreateCourse() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseCreateDto request = new CourseCreateDto("Математика", "Описание", teacher.id());

        ResponseEntity<CourseDto> response = restTemplate.postForEntity(
                url("/api/v1/courses"), request, CourseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Математика");
        assertThat(response.getBody().teacherId()).isEqualTo(teacher.id());
    }

    @Test
    void shouldReturn400WhenNameIsBlank() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseCreateDto invalid = new CourseCreateDto("", "Описание", teacher.id());

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/courses"), invalid, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn404WhenTeacherNotFound() {
        CourseCreateDto request = new CourseCreateDto("Математика", "Описание", 99999L);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/courses"), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnCourseById() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto created = createCourse("Математика", teacher.id());

        ResponseEntity<CourseDto> response = restTemplate.getForEntity(
                url("/api/v1/courses/" + created.id()), CourseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(created.id());
        assertThat(response.getBody().teacherId()).isEqualTo(teacher.id());
    }

    @Test
    void shouldReturn404WhenCourseNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/courses/999"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateCourse() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto created = createCourse("Математика", teacher.id());
        CourseUpdateDto update = new CourseUpdateDto("Физика", "Описание", teacher.id());

        ResponseEntity<CourseDto> response = restTemplate.exchange(
                url("/api/v1/courses/" + created.id()),
                HttpMethod.PUT,
                new HttpEntity<>(update),
                CourseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Физика");
    }

    @Test
    void shouldDeleteCourse() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto created = createCourse("Математика", teacher.id());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/api/v1/courses/" + created.id()),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                url("/api/v1/courses/" + created.id()), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFilterCoursesByTeacherId() {
        TeacherDto teacherA = createTeacher("Иван", "Иванов");
        TeacherDto teacherB = createTeacher("Пётр", "Петров");
        createCourse("Математика", teacherA.id());
        createCourse("Физика", teacherA.id());
        createCourse("Химия", teacherB.id());

        ResponseEntity<PageResponse<CourseDto>> response = restTemplate.exchange(
                url("/api/v1/courses?teacherId=" + teacherA.id()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSize(2);
        assertThat(response.getBody().content())
                .allMatch(c -> c.teacherId().equals(teacherA.id()));
    }
}