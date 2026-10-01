package com.university.learningmanagementsystem.specification;

import com.university.learningmanagementsystem.dto.student.StudentFilter;
import com.university.learningmanagementsystem.entity.Student;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class StudentSpecifications {

    private StudentSpecifications() {
    }

    public static Specification<Student> byFilter(StudentFilter filter) {
        return (root, query, cb) -> {
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

            if (filter.groupId() != null) {
                query.distinct(true);
                predicates.add(
                        cb.equal(root.join("groups").get("id"), filter.groupId())
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}