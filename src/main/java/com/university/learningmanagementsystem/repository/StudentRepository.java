package com.university.learningmanagementsystem.repository;

import com.university.learningmanagementsystem.entity.Student;
import com.university.learningmanagementsystem.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    Set<Student> findAllByIdIn(Set<Long> ids);

    default Student requireById(Long id) {
        return findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Студента с id: " + id + " не существует!"));
    }
}
