package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.TestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.group.GroupUpdateDto;
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
class GroupControllerTest {

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

    @Test
    void shouldCreateGroup() {
        GroupCreateDto request = new GroupCreateDto("Группа A", Set.of());

        ResponseEntity<GroupDto> response = restTemplate.postForEntity(
                url("/api/v1/groups"), request, GroupDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Группа A");
        assertThat(response.getBody().studentIds()).isEmpty();
    }

    @Test
    void shouldReturn400WhenNameIsBlank() {
        GroupCreateDto invalid = new GroupCreateDto("", Set.of());

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/groups"), invalid, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnGroupById() {
        GroupDto created = createGroup("Группа A");

        ResponseEntity<GroupDto> response = restTemplate.getForEntity(
                url("/api/v1/groups/" + created.id()), GroupDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(created.id());
        assertThat(response.getBody().name()).isEqualTo("Группа A");
    }

    @Test
    void shouldReturn404WhenGroupNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/groups/99999"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateGroup() {
        GroupDto created = createGroup("Группа A");
        GroupUpdateDto update = new GroupUpdateDto("Группа Б", Set.of());

        ResponseEntity<GroupDto> response = restTemplate.exchange(
                url("/api/v1/groups/" + created.id()),
                HttpMethod.PUT,
                new HttpEntity<>(update),
                GroupDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Группа Б");
    }

    @Test
    void shouldDeleteGroup() {
        GroupDto created = createGroup("Группа A");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/api/v1/groups/" + created.id()),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                url("/api/v1/groups/" + created.id()), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFilterGroupsByName() {
        createGroup("Java Max");
        createGroup("Java");
        createGroup("С++");

        ResponseEntity<PageResponse<GroupDto>> response = restTemplate.exchange(
                url("/api/v1/groups?name=Java"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSize(2);
        assertThat(response.getBody().content())
                .allMatch(g -> g.name().toLowerCase().contains("java"));
    }

    @Test
    void shouldFilterCaseInsensitive() {
        createGroup("Python");
        createGroup("Java");

        ResponseEntity<PageResponse<GroupDto>> response = restTemplate.exchange(
                url("/api/v1/groups?name=python"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSize(1);
        assertThat(response.getBody().content().get(0).name()).isEqualTo("Python");
    }
}