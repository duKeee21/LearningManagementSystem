package com.university.learningmanagementsystem.repository;

import com.university.learningmanagementsystem.entity.Schedule;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, JpaSpecificationExecutor<Schedule> {

    List<Schedule> findByGroupId(Long groupId);

    List<Schedule> findByTeacherId(Long teacherId);

    int deleteByEndDateBefore(LocalDateTime cutoff);

    default Schedule requireById(Long id) {
        return findById(id).orElseThrow(() ->
                new EntityNotFoundException("Расписания с id: " + id + " не существует!"));
    }
}