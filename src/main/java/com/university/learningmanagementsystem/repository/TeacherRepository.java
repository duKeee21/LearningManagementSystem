package com.university.learningmanagementsystem.repository;

import com.university.learningmanagementsystem.entity.Teacher;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {
    default Teacher requireById(Long id) {
        return findById(id).orElseThrow(() ->
                new EntityNotFoundException("Учителя с id: " + id + " не существует!"));
    }
}