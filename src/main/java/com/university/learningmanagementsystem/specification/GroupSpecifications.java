package com.university.learningmanagementsystem.specification;

import com.university.learningmanagementsystem.dto.group.GroupFilter;
import com.university.learningmanagementsystem.entity.Group;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class GroupSpecifications {

    private GroupSpecifications() {
    }

    public static Specification<Group> byFilter(GroupFilter filter) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.name() != null && !filter.name().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("name")),
                                "%" + filter.name().toLowerCase() + "%")
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
