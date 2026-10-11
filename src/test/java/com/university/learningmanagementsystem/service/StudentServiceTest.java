package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.config.IntegrationTestConfig;
import com.university.learningmanagementsystem.dto.student.StudentCreateDto;
import com.university.learningmanagementsystem.dto.student.StudentDto;
import com.university.learningmanagementsystem.dto.student.StudentUpdateDto;
import com.university.learningmanagementsystem.entity.Group;
import com.university.learningmanagementsystem.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StudentServiceTest extends IntegrationTestConfig {

    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupRepository groupRepository;

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