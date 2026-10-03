package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.entity.Student;
import com.university.learningmanagementsystem.repository.StudentRepository;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class GroupServiceTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private GroupService groupService;

    @Autowired
    private StudentRepository studentRepository;

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

    @Test
    void shouldCreateEmptyGroup() {
        GroupDto saved = groupService.create(new GroupCreateDto("A", Set.of()));

        assertThat(saved.id()).isNotNull();
        assertThat(saved.name()).isEqualTo("A");
        assertThat(saved.studentIds()).isEmpty();
    }

    @Test
    void shouldAddStudentsToGroup() {
        Student student = studentRepository.save(
                Student.builder().firstName("Иван").lastName("Иванов").build());

        GroupDto saved = groupService.create(new GroupCreateDto("A", Set.of(student.getId())));

        assertThat(saved.studentIds()).containsExactly(student.getId());
    }

    @Test
    void shouldSynchronizeStudentsOnGroupCreate() {
        Student student = studentRepository.save(
                Student.builder().firstName("Иван").lastName("Иванов").build());

        groupService.create(new GroupCreateDto("A", Set.of(student.getId())));

        Integer links = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_group WHERE student_id = ?",
                Integer.class,
                student.getId()
        );
        assertThat(links).isEqualTo(1);
    }

    @Test
    void shouldThrowWhenStudentNotFound() {
        assertThatThrownBy(() -> groupService.create(new GroupCreateDto("A", Set.of(99999L))))
                .isInstanceOf(RuntimeException.class);
    }
}