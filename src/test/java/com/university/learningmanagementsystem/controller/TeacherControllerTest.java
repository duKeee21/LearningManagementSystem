package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.TestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherUpdateDto;
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
class TeacherControllerTest {

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
        jdbcTemplate.execute("DELETE FROM teachers");
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("DELETE FROM teachers");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private TeacherDto createTeacher(String firstName, String lastName) {
        TeacherCreateDto request = new TeacherCreateDto(firstName, lastName);
        ResponseEntity<TeacherDto> response = restTemplate.postForEntity(
                url("/api/v1/teachers"), request, TeacherDto.class);
        return response.getBody();
    }

    @Test
    void shouldCreateTeacher() {
        TeacherCreateDto request = new TeacherCreateDto("Иван", "Иванов");

        ResponseEntity<TeacherDto> response = restTemplate.postForEntity(
                url("/api/v1/teachers"), request, TeacherDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("Иван");
        assertThat(response.getBody().lastName()).isEqualTo("Иванов");
    }

    @Test
    void shouldReturnAllTeachers() {
        createTeacher("Иван", "Иванов");
        createTeacher("Пётр", "Петров");

        ResponseEntity<PageResponse<TeacherDto>> response = restTemplate.exchange(
                url("/api/v1/teachers"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSize(2);
        assertThat(response.getBody().totalElements()).isEqualTo(2);
    }

    @Test
    void shouldReturnTeacherById() {
        TeacherDto created = createTeacher("Иван", "Иванов");

        ResponseEntity<TeacherDto> response = restTemplate.getForEntity(
                url("/api/v1/teachers/" + created.id()), TeacherDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(created.id());
        assertThat(response.getBody().firstName()).isEqualTo("Иван");
    }

    @Test
    void shouldReturn404WhenTeacherNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/teachers/99999"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateTeacher() {
        TeacherDto created = createTeacher("Иван", "Иванов");
        TeacherUpdateDto update = new TeacherUpdateDto("Пётр", "Петров");

        ResponseEntity<TeacherDto> response = restTemplate.exchange(
                url("/api/v1/teachers/" + created.id()),
                HttpMethod.PUT,
                new HttpEntity<>(update),
                TeacherDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("Пётр");
        assertThat(response.getBody().lastName()).isEqualTo("Петров");
    }

    @Test
    void shouldDeleteTeacher() {
        TeacherDto created = createTeacher("Иван", "Иванов");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/api/v1/teachers/" + created.id()),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                url("/api/v1/teachers/" + created.id()), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn400WhenFirstNameIsBlank() {
        TeacherCreateDto invalid = new TeacherCreateDto("", "Иванов");

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/teachers"), invalid, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldFilterTeachersByLastName() {
        createTeacher("Иван", "Иванов");
        createTeacher("Пётр", "Петров");
        createTeacher("Антон", "Иванов");

        ResponseEntity<PageResponse<TeacherDto>> response = restTemplate.exchange(
                url("/api/v1/teachers?lastName=Иванов"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSize(2);
        assertThat(response.getBody().content())
                .allMatch(t -> t.lastName().equals("Иванов"));
    }
}