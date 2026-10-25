package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.IntegrationTestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.course.CourseCreateDto;
import com.university.learningmanagementsystem.dto.course.CourseDto;
import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleCreateDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScheduleControllerTest extends IntegrationTestConfig {

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

    private ScheduleDto createSchedule(Long groupId, Long teacherId, Long courseId,
                                       LocalDateTime start, LocalDateTime end) throws Exception {
        ScheduleCreateDto request = new ScheduleCreateDto(groupId, teacherId, courseId, start, end);
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return jsonMapper.readValue(result.getResponse().getContentAsString(), ScheduleDto.class);
    }

    private LocalDateTime defaultStart() {
        return LocalDateTime.of(2026, 9, 1, 10, 0);
    }

    private LocalDateTime defaultEnd() {
        return LocalDateTime.of(2026, 9, 1, 11, 30);
    }

    @Test
    void shouldCreateSchedule() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        GroupDto group = createGroup("Группа A");
        CourseDto course = createCourse("Математика", teacher.id());

        ScheduleCreateDto request = new ScheduleCreateDto(
                group.id(), teacher.id(), course.id(), defaultStart(), defaultEnd());
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), ScheduleDto.class);

        assertThat(response.id()).isNotNull();
        assertThat(response.groupId()).isEqualTo(group.id());
        assertThat(response.teacherId()).isEqualTo(teacher.id());
        assertThat(response.courseId()).isEqualTo(course.id());
    }

    @Test
    void shouldReturn400WhenGroupIdIsNull() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto course = createCourse("Математика", teacher.id());
        ScheduleCreateDto invalid = new ScheduleCreateDto(
                null, teacher.id(), course.id(), defaultStart(), defaultEnd());
        String json = jsonMapper.writeValueAsString(invalid);

        mockMvc.perform(post("/api/v1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenGroupNotFound() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        CourseDto course = createCourse("Математика", teacher.id());
        ScheduleCreateDto request = new ScheduleCreateDto(
                99999L, teacher.id(), course.id(), defaultStart(), defaultEnd());
        String json = jsonMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnScheduleById() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        GroupDto group = createGroup("Группа A");
        CourseDto course = createCourse("Математика", teacher.id());
        ScheduleDto created = createSchedule(group.id(), teacher.id(), course.id(),
                defaultStart(), defaultEnd());

        MvcResult result = mockMvc.perform(get("/api/v1/schedules/" + created.id()))
                .andExpect(status().isOk())
                .andReturn();

        ScheduleDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), ScheduleDto.class);

        assertThat(response.id()).isEqualTo(created.id());
    }

    @Test
    void shouldReturn404WhenScheduleNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/schedules/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterSchedulesByGroupId() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        GroupDto groupA = createGroup("Группа A");
        GroupDto groupB = createGroup("Группа B");
        CourseDto course = createCourse("Математика", teacher.id());

        createSchedule(groupA.id(), teacher.id(), course.id(), defaultStart(), defaultEnd());
        createSchedule(groupA.id(), teacher.id(), course.id(),
                defaultStart().plusDays(1), defaultEnd().plusDays(1));
        createSchedule(groupB.id(), teacher.id(), course.id(),
                defaultStart().plusDays(2), defaultEnd().plusDays(2));

        MvcResult result = mockMvc.perform(get("/api/v1/schedules")
                        .param("groupId", String.valueOf(groupA.id())))
                .andExpect(status().isOk())
                .andReturn();

        PageResponse<ScheduleDto> page = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(page.content()).hasSize(2);
        assertThat(page.content())
                .allMatch(s -> s.groupId().equals(groupA.id()));
    }

    @Test
    void shouldFilterSchedulesByTeacherId() throws Exception {
        TeacherDto teacherA = createTeacher("Иван", "Иванов");
        TeacherDto teacherB = createTeacher("Пётр", "Петров");
        GroupDto group = createGroup("Группа A");
        CourseDto courseA = createCourse("Математика", teacherA.id());
        CourseDto courseB = createCourse("Физика", teacherB.id());

        createSchedule(group.id(), teacherA.id(), courseA.id(), defaultStart(), defaultEnd());
        createSchedule(group.id(), teacherB.id(), courseB.id(),
                defaultStart().plusDays(1), defaultEnd().plusDays(1));

        MvcResult result = mockMvc.perform(get("/api/v1/schedules")
                        .param("teacherId", String.valueOf(teacherA.id())))
                .andExpect(status().isOk())
                .andReturn();

        PageResponse<ScheduleDto> page = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(page.content()).hasSize(1);
        assertThat(page.content().getFirst().teacherId()).isEqualTo(teacherA.id());
    }

    @Test
    void shouldDeleteSchedule() throws Exception {
        TeacherDto teacher = createTeacher("Иван", "Иванов");
        GroupDto group = createGroup("Группа A");
        CourseDto course = createCourse("Математика", teacher.id());
        ScheduleDto created = createSchedule(group.id(), teacher.id(), course.id(),
                defaultStart(), defaultEnd());

        mockMvc.perform(delete("/api/v1/schedules/" + created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/schedules/" + created.id()))
                .andExpect(status().isNotFound());
    }
}