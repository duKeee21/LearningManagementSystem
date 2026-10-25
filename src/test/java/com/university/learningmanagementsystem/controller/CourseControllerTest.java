package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.IntegrationTestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.course.CourseCreateDto;
import com.university.learningmanagementsystem.dto.course.CourseDto;
import com.university.learningmanagementsystem.dto.course.CourseUpdateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
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

class CourseControllerTest extends IntegrationTestConfig {

    private TeacherDto createTeacher(String firstName, String lastName) throws Exception {
        TeacherCreateDto request = new TeacherCreateDto(firstName, lastName);
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return jsonMapper.readValue(result.getResponse().getContentAsString(), TeacherDto.class);
    }

    private CourseDto createCourse(String name, Long teacherId) throws Exception {
        CourseCreateDto request = new CourseCreateDto(name, "Описание", teacherId);
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return jsonMapper.readValue(result.getResponse().getContentAsString(), CourseDto.class);
    }

    @Test
    void shouldCreateCourse() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseCreateDto request = new CourseCreateDto("Математика", "Описание", teacher.id());
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        CourseDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), CourseDto.class);

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("Математика");
        assertThat(response.teacherId()).isEqualTo(teacher.id());
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseCreateDto invalid = new CourseCreateDto("", "Описание", teacher.id());
        String json = jsonMapper.writeValueAsString(invalid);

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenTeacherNotFound() throws Exception {
        CourseCreateDto request = new CourseCreateDto("Математика", "Описание", 99999L);
        String json = jsonMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCourseById() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto created = createCourse("Математика", teacher.id());

        MvcResult result = mockMvc.perform(get("/api/v1/courses/" + created.id()))
                .andExpect(status().isOk())
                .andReturn();

        CourseDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), CourseDto.class);

        assertThat(response.id()).isEqualTo(created.id());
        assertThat(response.teacherId()).isEqualTo(teacher.id());
    }

    @Test
    void shouldReturn404WhenCourseNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/courses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateCourse() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto created = createCourse("Математика", teacher.id());
        CourseUpdateDto update = new CourseUpdateDto("Физика", "Описание", teacher.id());
        String json = jsonMapper.writeValueAsString(update);

        MvcResult result = mockMvc.perform(put("/api/v1/courses/" + created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        CourseDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), CourseDto.class);

        assertThat(response.name()).isEqualTo("Физика");
    }

    @Test
    void shouldDeleteCourse() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto created = createCourse("Математика", teacher.id());

        mockMvc.perform(delete("/api/v1/courses/" + created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/courses/" + created.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterCoursesByTeacherId() throws Exception {
        TeacherDto teacherA = createTeacher("Иван", "Иванов");
        TeacherDto teacherB = createTeacher("Пётр", "Петров");
        createCourse("Математика", teacherA.id());
        createCourse("Физика", teacherA.id());
        createCourse("Химия", teacherB.id());

        MvcResult result = mockMvc.perform(get("/api/v1/courses")
                        .param("teacherId", String.valueOf(teacherA.id())))
                .andExpect(status().isOk())
                .andReturn();

        PageResponse<CourseDto> page = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(page.content()).hasSize(2);
        assertThat(page.content())
                .allMatch(c -> c.teacherId().equals(teacherA.id()));
    }
}