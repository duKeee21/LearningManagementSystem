package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.TestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.course.CourseCreateDto;
import com.university.learningmanagementsystem.dto.course.CourseDto;
import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleCreateDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleDto;
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

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestConfig.class)
class ScheduleControllerTest {

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
        return restTemplate.postForEntity(
                url("/api/v1/teachers"),
                new TeacherCreateDto(firstName, lastName),
                TeacherDto.class).getBody();
    }

    private GroupDto createGroup(String name) {
        return restTemplate.postForEntity(
                url("/api/v1/groups"),
                new GroupCreateDto(name, Set.of()),
                GroupDto.class).getBody();
    }

    private CourseDto createCourse(String name, Long teacherId) {
        return restTemplate.postForEntity(
                url("/api/v1/courses"),
                new CourseCreateDto(name, "Описание", teacherId),
                CourseDto.class).getBody();
    }

    private ScheduleDto createSchedule(Long groupId, Long teacherId, Long courseId,
                                       LocalDateTime start, LocalDateTime end) {
        ScheduleCreateDto request = new ScheduleCreateDto(groupId, teacherId, courseId, start, end);
        return restTemplate.postForEntity(
                url("/api/v1/schedules"), request, ScheduleDto.class).getBody();
    }

    private LocalDateTime defaultStart() {
        return LocalDateTime.of(2026, 9, 1, 10, 0);
    }

    private LocalDateTime defaultEnd() {
        return LocalDateTime.of(2026, 9, 1, 11, 30);
    }

    @Test
    void shouldCreateSchedule() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        GroupDto group = createGroup("Группа A");
        CourseDto course = createCourse("Математика", teacher.id());

        ScheduleCreateDto request = new ScheduleCreateDto(
                group.id(), teacher.id(), course.id(), defaultStart(), defaultEnd());

        ResponseEntity<ScheduleDto> response = restTemplate.postForEntity(
                url("/api/v1/schedules"), request, ScheduleDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().groupId()).isEqualTo(group.id());
        assertThat(response.getBody().teacherId()).isEqualTo(teacher.id());
        assertThat(response.getBody().courseId()).isEqualTo(course.id());
    }

    @Test
    void shouldReturn400WhenGroupIdIsNull() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto course = createCourse("Математика", teacher.id());
        ScheduleCreateDto invalid = new ScheduleCreateDto(
                null, teacher.id(), course.id(), defaultStart(), defaultEnd());

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/schedules"), invalid, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn404WhenGroupNotFound() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto course = createCourse("Математика", teacher.id());
        ScheduleCreateDto request = new ScheduleCreateDto(
                99999L, teacher.id(), course.id(), defaultStart(), defaultEnd());

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/schedules"), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnScheduleById() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        GroupDto group = createGroup("Группа A");
        CourseDto course = createCourse("Математика", teacher.id());
        ScheduleDto created = createSchedule(group.id(), teacher.id(), course.id(),
                defaultStart(), defaultEnd());

        ResponseEntity<ScheduleDto> response = restTemplate.getForEntity(
                url("/api/v1/schedules/" + created.id()), ScheduleDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(created.id());
    }

    @Test
    void shouldReturn404WhenScheduleNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/schedules/99999"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFilterSchedulesByGroupId() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        GroupDto groupA = createGroup("Группа A");
        GroupDto groupB = createGroup("Группа B");
        CourseDto course = createCourse("Математика", teacher.id());

        createSchedule(groupA.id(), teacher.id(), course.id(), defaultStart(), defaultEnd());
        createSchedule(groupA.id(), teacher.id(), course.id(),
                defaultStart().plusDays(1), defaultEnd().plusDays(1));
        createSchedule(groupB.id(), teacher.id(), course.id(),
                defaultStart().plusDays(2), defaultEnd().plusDays(2));

        ResponseEntity<PageResponse<ScheduleDto>> response = restTemplate.exchange(
                url("/api/v1/schedules?groupId=" + groupA.id()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSize(2);
        assertThat(response.getBody().content())
                .allMatch(s -> s.groupId().equals(groupA.id()));
    }

    @Test
    void shouldFilterSchedulesByTeacherId() {
        TeacherDto teacherA = createTeacher("Иван", "Иванов");
        TeacherDto teacherB = createTeacher("Пётр", "Петров");
        GroupDto group = createGroup("Группа A");
        CourseDto courseA = createCourse("Математика", teacherA.id());
        CourseDto courseB = createCourse("Физика", teacherB.id());

        createSchedule(group.id(), teacherA.id(), courseA.id(), defaultStart(), defaultEnd());
        createSchedule(group.id(), teacherB.id(), courseB.id(),
                defaultStart().plusDays(1), defaultEnd().plusDays(1));

        ResponseEntity<PageResponse<ScheduleDto>> response = restTemplate.exchange(
                url("/api/v1/schedules?teacherId=" + teacherA.id()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSize(1);
        assertThat(response.getBody().content().get(0).teacherId()).isEqualTo(teacherA.id());
    }

    @Test
    void shouldDeleteSchedule() {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        GroupDto group = createGroup("Группа A");
        CourseDto course = createCourse("Математика", teacher.id());
        ScheduleDto created = createSchedule(group.id(), teacher.id(), course.id(),
                defaultStart(), defaultEnd());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/api/v1/schedules/" + created.id()),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                url("/api/v1/schedules/" + created.id()), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}