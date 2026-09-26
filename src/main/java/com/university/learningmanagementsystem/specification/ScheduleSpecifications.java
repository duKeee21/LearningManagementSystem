package com.university.learningmanagementsystem.specification;

import com.university.learningmanagementsystem.dto.schedule.ScheduleFilter;
import com.university.learningmanagementsystem.entity.Schedule;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ScheduleSpecifications {

    private ScheduleSpecifications() {
    }

    public static Specification<Schedule> byFilter(ScheduleFilter filter) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.groupId() != null) {
                predicates.add(
                        cb.equal(root.get("group").get("id"), filter.groupId())
                );
            }
            if (filter.teacherId() != null) {
                predicates.add(
                        cb.equal(root.get("teacher").get("id"), filter.teacherId())
                );
            }
            if (filter.courseId() != null) {
                predicates.add(
                        cb.equal(root.get("course").get("id"), filter.courseId())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}