package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.student.StudentCreateDto;
import com.university.learningmanagementsystem.dto.student.StudentDto;
import com.university.learningmanagementsystem.dto.student.StudentUpdateDto;
import com.university.learningmanagementsystem.entity.Group;
import com.university.learningmanagementsystem.repository.GroupRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class StudentServiceTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupRepository groupRepository;

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

    private Group createGroup(String name) {
        return groupRepository.save(Group.builder().name(name).build());
    }

    @Test
    void shouldThrowWhenGroupIdsEmpty() {
        StudentCreateDto request = new StudentCreateDto("Иван", "Иванов", Set.of());

        assertThatThrownBy(() -> studentService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("минимум");
    }

    @Test
    void shouldThrowWhenGroupNotFound() {
        StudentCreateDto request = new StudentCreateDto("Иван", "Иванов", Set.of(10000L));

        assertThatThrownBy(() -> studentService.create(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateStudentWithGroups() {
        Group group = createGroup("A");
        StudentCreateDto request = new StudentCreateDto("Иван", "Иванов", Set.of(group.getId()));

        StudentDto saved = studentService.create(request);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.firstName()).isEqualTo("Иван");
        assertThat(saved.groupIds()).containsExactly(group.getId());
    }

    @Test
    void shouldUpdateStudentGroups() {
        Group groupA = createGroup("A");
        Group groupB = createGroup("B");
        StudentDto created = studentService.create(
                new StudentCreateDto("Иван", "Иванов", Set.of(groupA.getId())));

        StudentDto updated = studentService.update(created.id(),
                new StudentUpdateDto("Иван", "Иванов", Set.of(groupB.getId())));

        assertThat(updated.groupIds()).containsExactly(groupB.getId());
    }

    @Test
    void shouldFindStudentById() {
        Group group = createGroup("A");
        StudentDto created = studentService.create(
                new StudentCreateDto("Иван", "Иванов", Set.of(group.getId())));

        StudentDto found = studentService.findById(created.id());

        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.groupIds()).containsExactly(group.getId());
    }
}