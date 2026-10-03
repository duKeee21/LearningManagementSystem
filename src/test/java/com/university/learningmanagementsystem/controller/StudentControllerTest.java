package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.TestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.student.StudentCreateDto;
import com.university.learningmanagementsystem.dto.student.StudentDto;
import com.university.learningmanagementsystem.dto.student.StudentUpdateDto;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestConfig.class)
class StudentControllerTest {

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
        jdbcTemplate.execute("DELETE FROM student_group");
        jdbcTemplate.execute("DELETE FROM students");
        jdbcTemplate.execute("DELETE FROM groups");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private GroupDto createGroup(String name) {
        GroupCreateDto request = new GroupCreateDto(name, Set.of());
        ResponseEntity<GroupDto> response = restTemplate.postForEntity(
                url("/api/v1/groups"), request, GroupDto.class);
        return response.getBody();
    }

    private StudentDto createStudent(String firstName, String lastName, Set<Long> groupIds) {
        StudentCreateDto request = new StudentCreateDto(firstName, lastName, groupIds);
        ResponseEntity<StudentDto> response = restTemplate.postForEntity(
                url("/api/v1/students"), request, StudentDto.class);
        return response.getBody();
    }

    @Test
    void shouldCreateStudent() {
        GroupDto group = createGroup("Группа A");

        StudentCreateDto request = new StudentCreateDto("Иван", "Иванов", Set.of(group.id()));

        ResponseEntity<StudentDto> response = restTemplate.postForEntity(
                url("/api/v1/students"), request, StudentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("Иван");
        assertThat(response.getBody().lastName()).isEqualTo("Иванов");
        assertThat(response.getBody().groupIds()).containsExactly(group.id());
    }

    @Test
    void shouldReturn400WhenGroupIdsEmpty() {
        StudentCreateDto invalid = new StudentCreateDto("Иван", "Иванов", Set.of());

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/students"), invalid, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn404WhenGroupNotFound() {
        StudentCreateDto request = new StudentCreateDto("Иван", "Иванов", Set.of(99999L));

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/students"), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnStudentById() {
        GroupDto group = createGroup("Группа A");
        StudentDto created = createStudent("Иван", "Иванов", Set.of(group.id()));

        ResponseEntity<StudentDto> response = restTemplate.getForEntity(
                url("/api/v1/students/" + created.id()), StudentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(created.id());
        assertThat(response.getBody().groupIds()).containsExactly(group.id());
    }

    @Test
    void shouldReturn404WhenStudentNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/students/99999"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateStudent() {
        GroupDto groupA = createGroup("Группа A");
        GroupDto groupB = createGroup("Группа Б");
        StudentDto created = createStudent("Иван", "Иванов", Set.of(groupA.id()));

        StudentUpdateDto update = new StudentUpdateDto("Пётр", "Петров", Set.of(groupB.id()));

        ResponseEntity<StudentDto> response = restTemplate.exchange(
                url("/api/v1/students/" + created.id()),
                HttpMethod.PUT,
                new HttpEntity<>(update),
                StudentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("Пётр");
        assertThat(response.getBody().groupIds()).containsExactly(groupB.id());
    }

    @Test
    void shouldDeleteStudent() {
        GroupDto group = createGroup("Группа A");
        StudentDto created = createStudent("Иван", "Иванов", Set.of(group.id()));

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/api/v1/students/" + created.id()),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                url("/api/v1/students/" + created.id()), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFilterStudentsByGroupId() {
        GroupDto groupA = createGroup("Группа A");
        GroupDto groupB = createGroup("Группа Б");
        createStudent("Иван", "Иванов", Set.of(groupA.id()));
        createStudent("Пётр", "Петров", Set.of(groupA.id()));
        createStudent("Антон", "Антонов", Set.of(groupB.id()));

        ResponseEntity<PageResponse<StudentDto>> response = restTemplate.exchange(
                url("/api/v1/students?groupId=" + groupA.id()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSize(2);
        assertThat(response.getBody().content())
                .allMatch(s -> s.groupIds().contains(groupA.id()));
    }
}