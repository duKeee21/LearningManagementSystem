package com.university.learningmanagementsystem.repository;

import com.university.learningmanagementsystem.entity.Group;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {
    Set<Group> findAllByIdIn(Set<Long> ids);

    default Group requireById(Long id) {
        return findById(id).orElseThrow(() ->
                new EntityNotFoundException("Группы с id: " + id + " не существует!"));
    }
}
