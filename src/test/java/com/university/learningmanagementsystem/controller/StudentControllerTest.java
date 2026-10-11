package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.IntegrationTestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.student.StudentCreateDto;
import com.university.learningmanagementsystem.dto.student.StudentDto;
import com.university.learningmanagementsystem.dto.student.StudentUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentControllerTest extends IntegrationTestConfig {

    private GroupDto createGroup(String name) throws Exception {
        GroupCreateDto request = new GroupCreateDto(name, Set.of());
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return jsonMapper.readValue(result.getResponse().getContentAsString(), GroupDto.class);
    }

    private StudentDto createStudent(String firstName, String lastName, Set<Long> groupIds) throws Exception {
        StudentCreateDto request = new StudentCreateDto(firstName, lastName, groupIds);
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return jsonMapper.readValue(result.getResponse().getContentAsString(), StudentDto.class);
    }

    @Test
    void shouldCreateStudent() throws Exception {
        GroupDto group = createGroup("Группа A");
        StudentCreateDto request = new StudentCreateDto("Иван", "Иванов", Set.of(group.id()));
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        StudentDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), StudentDto.class);

        assertThat(response.id()).isNotNull();
        assertThat(response.firstName()).isEqualTo("Иван");
        assertThat(response.groupIds()).containsExactly(group.id());
    }

    @Test
    void shouldReturn400WhenGroupIdsEmpty() throws Exception {
        StudentCreateDto invalid = new StudentCreateDto("Иван", "Иванов", Set.of());
        String json = jsonMapper.writeValueAsString(invalid);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenGroupNotFound() throws Exception {
        StudentCreateDto request = new StudentCreateDto("Иван", "Иванов", Set.of(99999L));
        String json = jsonMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnStudentById() throws Exception {
        GroupDto group = createGroup("Группа A");
        StudentDto created = createStudent("Иван", "Иванов", Set.of(group.id()));

        MvcResult result = mockMvc.perform(get("/api/v1/students/" + created.id()))
                .andExpect(status().isOk())
                .andReturn();

        StudentDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), StudentDto.class);

        assertThat(response.id()).isEqualTo(created.id());
        assertThat(response.groupIds()).containsExactly(group.id());
    }

    @Test
    void shouldReturn404WhenStudentNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/students/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        GroupDto groupA = createGroup("Группа A");
        GroupDto groupB = createGroup("Группа Б");
        StudentDto created = createStudent("Иван", "Иванов", Set.of(groupA.id()));

        StudentUpdateDto update = new StudentUpdateDto("Пётр", "Петров", Set.of(groupB.id()));
        String json = jsonMapper.writeValueAsString(update);

        MvcResult result = mockMvc.perform(put("/api/v1/students/" + created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        StudentDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), StudentDto.class);

        assertThat(response.firstName()).isEqualTo("Пётр");
        assertThat(response.groupIds()).containsExactly(groupB.id());
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        GroupDto group = createGroup("Группа A");
        StudentDto created = createStudent("Иван", "Иванов", Set.of(group.id()));

        mockMvc.perform(delete("/api/v1/students/" + created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/students/" + created.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterStudentsByGroupId() throws Exception {
        GroupDto groupA = createGroup("Группа A");
        GroupDto groupB = createGroup("Группа Б");
        createStudent("Иван", "Иванов", Set.of(groupA.id()));
        createStudent("Пётр", "Петров", Set.of(groupA.id()));
        createStudent("Антон", "Антонов", Set.of(groupB.id()));

        MvcResult result = mockMvc.perform(get("/api/v1/students")
                        .param("groupId", String.valueOf(groupA.id())))
                .andExpect(status().isOk())
                .andReturn();

        PageResponse<StudentDto> page = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(page.content()).hasSize(2);
        assertThat(page.content()).allMatch(s -> s.groupIds().contains(groupA.id()));
    }
}