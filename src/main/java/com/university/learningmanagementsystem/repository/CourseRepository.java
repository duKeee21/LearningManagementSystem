package com.university.learningmanagementsystem.repository;

import com.university.learningmanagementsystem.entity.Course;
import com.university.learningmanagementsystem.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    default Course requireById(Long id) {
        return findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Курса с id: " + id + " не существует!"));
    }
}