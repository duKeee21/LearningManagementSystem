package com.university.learningmanagementsystem.controller;

import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.group.GroupCreateDto;
import com.university.learningmanagementsystem.dto.group.GroupDto;
import com.university.learningmanagementsystem.dto.group.GroupFilter;
import com.university.learningmanagementsystem.dto.group.GroupUpdateDto;
import com.university.learningmanagementsystem.service.GroupService;
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
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<PageResponse<GroupDto>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        GroupFilter filter = new GroupFilter(name);
        return ResponseEntity.ok(groupService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.findById(id));
    }

    @PostMapping
    public ResponseEntity<GroupDto> create(@Valid @RequestBody GroupCreateDto groupCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.create(groupCreateDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> update(
            @PathVariable Long id,
            @Valid @RequestBody GroupUpdateDto groupUpdateDto) {
        return ResponseEntity.ok(groupService.update(id, groupUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}