package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.config.IntegrationTestConfig;
import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.entity.Student;
import com.university.learningmanagementsystem.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupServiceTest extends IntegrationTestConfig {

    @Autowired
    private GroupService groupService;

    @Autowired
    private StudentRepository studentRepository;

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