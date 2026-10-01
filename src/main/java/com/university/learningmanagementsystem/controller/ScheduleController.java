package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.schedule.ScheduleCreateDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleFilter;
import com.university.learningmanagementsystem.dto.schedule.ScheduleUpdateDto;
import com.university.learningmanagementsystem.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<PageResponse<ScheduleDto>> findAll(
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").ascending());
        ScheduleFilter filter = new ScheduleFilter(groupId, teacherId, courseId);
        return ResponseEntity.ok(scheduleService.findAll(filter, pageable));
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