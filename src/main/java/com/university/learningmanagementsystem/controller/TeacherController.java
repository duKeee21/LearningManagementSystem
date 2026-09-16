package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.dto.Teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.Teacher.TeacherDto;
import com.university.learningmanagementsystem.dto.Teacher.TeacherUpdateDto;
import com.university.learningmanagementsystem.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<Page<TeacherDto>> findAll(
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {

        return ResponseEntity.ok(teacherService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TeacherDto> create(@Valid @RequestBody TeacherCreateDto teacherCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.create(teacherCreateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TeacherUpdateDto teacherUpdateDto) {
        return ResponseEntity.ok(teacherService.update(id, teacherUpdateDto));
    }
}
