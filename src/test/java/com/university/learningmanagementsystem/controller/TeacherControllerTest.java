package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.IntegrationTestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TeacherControllerTest extends IntegrationTestConfig {

    private TeacherDto createTeacher(String firstName, String lastName) throws Exception {
        TeacherCreateDto request = new TeacherCreateDto(firstName, lastName);
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return jsonMapper.readValue(
                result.getResponse().getContentAsString(), TeacherDto.class);
    }

    @Test
    void shouldCreateTeacher() throws Exception {
        TeacherCreateDto request = new TeacherCreateDto("Иван", "Иванов");
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        TeacherDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), TeacherDto.class);

        assertThat(response.id()).isNotNull();
        assertThat(response.firstName()).isEqualTo("Иван");
        assertThat(response.lastName()).isEqualTo("Иванов");
    }

    @Test
    void shouldReturnAllTeachers() throws Exception {
        createTeacher("Иван", "Иванов");
        createTeacher("Пётр", "Петров");

        MvcResult result = mockMvc.perform(get("/api/v1/teachers"))
                .andExpect(status().isOk())
                .andReturn();

        PageResponse<TeacherDto> page = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(page.content()).hasSize(2);
        assertThat(page.totalElements()).isEqualTo(2);
    }

    @Test
    void shouldReturnTeacherById() throws Exception {
        TeacherDto created = createTeacher("Иван", "Иванов");

        MvcResult result = mockMvc.perform(get("/api/v1/teachers/" + created.id()))
                .andExpect(status().isOk())
                .andReturn();

        TeacherDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), TeacherDto.class);

        assertThat(response.id()).isEqualTo(created.id());
        assertThat(response.firstName()).isEqualTo("Иван");
    }

    @Test
    void shouldReturn404WhenTeacherNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/teachers/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateTeacher() throws Exception {
        TeacherDto created = createTeacher("Иван", "Иванов");
        TeacherUpdateDto update = new TeacherUpdateDto("Пётр", "Петров");
        String json = jsonMapper.writeValueAsString(update);

        MvcResult result = mockMvc.perform(put("/api/v1/teachers/" + created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        TeacherDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), TeacherDto.class);

        assertThat(response.firstName()).isEqualTo("Пётр");
        assertThat(response.lastName()).isEqualTo("Петров");
    }

    @Test
    void shouldDeleteTeacher() throws Exception {
        TeacherDto created = createTeacher("Иван", "Иванов");

        mockMvc.perform(delete("/api/v1/teachers/" + created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/teachers/" + created.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenFirstNameIsBlank() throws Exception {
        TeacherCreateDto invalid = new TeacherCreateDto("", "Иванов");
        String json = jsonMapper.writeValueAsString(invalid);

        mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFilterTeachersByLastName() throws Exception {
        createTeacher("Иван", "Иванов");
        createTeacher("Пётр", "Петров");
        createTeacher("Антон", "Иванов");

        MvcResult result = mockMvc.perform(get("/api/v1/teachers")
                        .param("lastName", "Иванов"))
                .andExpect(status().isOk())
                .andReturn();

        PageResponse<TeacherDto> page = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(page.content()).hasSize(2);
        assertThat(page.content()).allMatch(t -> t.lastName().equals("Иванов"));
    }
}