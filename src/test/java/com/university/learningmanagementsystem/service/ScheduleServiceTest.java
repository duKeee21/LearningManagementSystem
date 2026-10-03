package com.university.learningmanagementsystem.service;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class ScheduleServiceTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private CourseService courseService;
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

    private TeacherDto createTeacher() {
        return teacherService.create(new TeacherCreateDto("Иван", "Иванов"));
    }

    private GroupDto createGroup() {
        return groupService.create(new GroupCreateDto("A", Set.of()));
    }

    private CourseDto createCourse(Long teacherId) {
        return courseService.create(new CourseCreateDto("Математика", "Описание", teacherId));
    }

    @Test
    void shouldCreateSchedule() {
        TeacherDto teacher = createTeacher();
        GroupDto group = createGroup();
        CourseDto course = createCourse(teacher.id());

        ScheduleDto saved = scheduleService.create(new ScheduleCreateDto(
                group.id(), teacher.id(), course.id(),
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 30)));

        assertThat(saved.id()).isNotNull();
        assertThat(saved.groupId()).isEqualTo(group.id());
        assertThat(saved.teacherId()).isEqualTo(teacher.id());
        assertThat(saved.courseId()).isEqualTo(course.id());
    }

    @Test
    void shouldThrowWhenGroupNotFound() {
        TeacherDto teacher = createTeacher();
        CourseDto course = createCourse(teacher.id());

        assertThatThrownBy(() -> scheduleService.create(new ScheduleCreateDto(
                99999L, teacher.id(), course.id(),
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 30))))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldThrowWhenTeacherNotFound() {
        GroupDto group = createGroup();

        assertThatThrownBy(() -> scheduleService.create(new ScheduleCreateDto(
                group.id(), 99999L, 99999L,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 30))))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldDeleteSchedule() {
        TeacherDto teacher = createTeacher();
        GroupDto group = createGroup();
        CourseDto course = createCourse(teacher.id());

        ScheduleDto created = scheduleService.create(new ScheduleCreateDto(
                group.id(), teacher.id(), course.id(),
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 30)));

        scheduleService.delete(created.id());

        assertThatThrownBy(() -> scheduleService.findById(created.id()))
                .isInstanceOf(RuntimeException.class);
    }
}