package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.teacher.TeacherCreateDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherDto;
import com.university.learningmanagementsystem.dto.teacher.TeacherFilter;
import com.university.learningmanagementsystem.dto.teacher.TeacherUpdateDto;
import com.university.learningmanagementsystem.service.TeacherService;
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

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<PageResponse<TeacherDto>> findAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        TeacherFilter filter = new TeacherFilter(firstName, lastName);
        return ResponseEntity.ok(teacherService.findAll(filter, pageable));
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
