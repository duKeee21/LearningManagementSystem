package com.university.learningmanagementsystem.specification;

import com.university.learningmanagementsystem.dto.course.CourseFilter;
import com.university.learningmanagementsystem.entity.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CourseSpecifications {

    private CourseSpecifications() {
    }

    public static Specification<Course> byFilter(CourseFilter filter) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.name() != null && !filter.name().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("name")),
                                "%" + filter.name().toLowerCase() + "%")
                );
            }
            if (filter.teacherId() != null) {
                predicates.add(
                        cb.equal(root.get("teacher").get("id"), filter.teacherId())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}