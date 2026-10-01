package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.student.StudentCreateDto;
import com.university.learningmanagementsystem.dto.student.StudentDto;
import com.university.learningmanagementsystem.dto.student.StudentFilter;
import com.university.learningmanagementsystem.dto.student.StudentUpdateDto;
import com.university.learningmanagementsystem.service.StudentService;
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
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<PageResponse<StudentDto>> findAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        StudentFilter filter = new StudentFilter(firstName, lastName, groupId);
        return ResponseEntity.ok(studentService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<StudentDto> create(@Valid @RequestBody StudentCreateDto studentCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.create(studentCreateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateDto studentUpdateDto) {
        return ResponseEntity.ok(studentService.update(id, studentUpdateDto));
    }
}