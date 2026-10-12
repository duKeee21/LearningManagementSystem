package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.config.IntegrationTestConfig;
import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.group.GroupUpdateDto;
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

class GroupControllerTest extends IntegrationTestConfig {

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

    @Test
    void shouldCreateGroup() throws Exception {
        GroupCreateDto request = new GroupCreateDto("Группа A", Set.of());
        String json = jsonMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        GroupDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), GroupDto.class);

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("Группа A");
        assertThat(response.studentIds()).isEmpty();
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        GroupCreateDto invalid = new GroupCreateDto("", Set.of());
        String json = jsonMapper.writeValueAsString(invalid);

        mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnGroupById() throws Exception {
        GroupDto created = createGroup("Группа A");

        MvcResult result = mockMvc.perform(get("/api/v1/groups/" + created.id()))
                .andExpect(status().isOk())
                .andReturn();

        GroupDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), GroupDto.class);

        assertThat(response.id()).isEqualTo(created.id());
        assertThat(response.name()).isEqualTo("Группа A");
    }

    @Test
    void shouldReturn404WhenGroupNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/groups/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateGroup() throws Exception {
        GroupDto created = createGroup("Группа A");
        GroupUpdateDto update = new GroupUpdateDto("Группа Б", Set.of());
        String json = jsonMapper.writeValueAsString(update);

        MvcResult result = mockMvc.perform(put("/api/v1/groups/" + created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        GroupDto response = jsonMapper.readValue(
                result.getResponse().getContentAsString(), GroupDto.class);

        assertThat(response.name()).isEqualTo("Группа Б");
    }

    @Test
    void shouldDeleteGroup() throws Exception {
        GroupDto created = createGroup("Группа A");

        mockMvc.perform(delete("/api/v1/groups/" + created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/groups/" + created.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterGroupsByName() throws Exception {
        createGroup("Java Max");
        createGroup("Java");
        createGroup("С++");

        MvcResult result = mockMvc.perform(get("/api/v1/groups")
                        .param("name", "Java"))
                .andExpect(status().isOk())
                .andReturn();

        PageResponse<GroupDto> page = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(page.content()).hasSize(2);
        assertThat(page.content())
                .allMatch(g -> g.name().toLowerCase().contains("java"));
    }

    @Test
    void shouldFilterCaseInsensitive() throws Exception {
        createGroup("Python");
        createGroup("Java");

        MvcResult result = mockMvc.perform(get("/api/v1/groups")
                        .param("name", "python"))
                .andExpect(status().isOk())
                .andReturn();

        PageResponse<GroupDto> page = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(page.content()).hasSize(1);
        assertThat(page.content().getFirst().name()).isEqualTo("Python");
    }
}