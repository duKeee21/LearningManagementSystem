package com.university.learningmanagementsystem.repository;

import com.university.learningmanagementsystem.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByGroupId(Long groupId);

    List<Schedule> findByTeacherId(Long teacherId);

    int deleteByEndDateBefore(LocalDateTime cutoff);
}