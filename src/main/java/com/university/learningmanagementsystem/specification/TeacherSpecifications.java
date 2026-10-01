package com.university.learningmanagementsystem.specification;

import com.university.learningmanagementsystem.dto.teacher.TeacherFilter;
import com.university.learningmanagementsystem.entity.Teacher;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class TeacherSpecifications {

    private TeacherSpecifications() {
    }

    public static Specification<Teacher> byFilter(TeacherFilter filter) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.firstName() != null && !filter.firstName().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("firstName")),
                                "%" + filter.firstName().toLowerCase() + "%")
                );
            }

            if (filter.lastName() != null && !filter.lastName().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("lastName")),
                                "%" + filter.lastName().toLowerCase() + "%")
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}