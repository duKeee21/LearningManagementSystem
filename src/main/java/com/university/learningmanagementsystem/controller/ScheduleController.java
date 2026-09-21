package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.dto.schedule.ScheduleCreateDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleUpdateDto;
import com.university.learningmanagementsystem.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<Page<ScheduleDto>> findAll(
            @PageableDefault(size = 20, sort = "startDate") Pageable pageable) {
        return ResponseEntity.ok(scheduleService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.findById(id));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ScheduleDto>> findByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(scheduleService.findByGroupId(groupId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ScheduleDto>> findByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(scheduleService.findByTeacherId(teacherId));
    }

    @PostMapping
    public ResponseEntity<ScheduleDto> create(@Valid @RequestBody ScheduleCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleUpdateDto dto) {
        return ResponseEntity.ok(scheduleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}