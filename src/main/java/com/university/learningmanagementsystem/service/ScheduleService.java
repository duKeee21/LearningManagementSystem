package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.schedule.ScheduleCreateDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleUpdateDto;
import com.university.learningmanagementsystem.entity.Course;
import com.university.learningmanagementsystem.entity.Group;
import com.university.learningmanagementsystem.entity.Schedule;
import com.university.learningmanagementsystem.entity.Teacher;
import com.university.learningmanagementsystem.mapper.ScheduleMapper;
import com.university.learningmanagementsystem.repository.CourseRepository;
import com.university.learningmanagementsystem.repository.GroupRepository;
import com.university.learningmanagementsystem.repository.ScheduleRepository;
import com.university.learningmanagementsystem.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final ScheduleMapper scheduleMapper;

    @Transactional(readOnly = true)
    public Page<ScheduleDto> findAll(Pageable pageable) {
        return scheduleRepository.findAll(pageable).map(scheduleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ScheduleDto findById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Расписания с id: " + id + " не существует!"));
        return scheduleMapper.toDto(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> findByGroupId(Long groupId) {
        return scheduleRepository.findByGroupId(groupId).stream()
                .map(scheduleMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> findByTeacherId(Long teacherId) {
        return scheduleRepository.findByTeacherId(teacherId).stream()
                .map(scheduleMapper::toDto)
                .toList();
    }

    @Transactional
    public ScheduleDto create(ScheduleCreateDto request) {
        Schedule schedule = scheduleMapper.toEntity(request);
        bindRelations(schedule, request.groupId(), request.teacherId(), request.courseId());
        Schedule saved = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Расписания с id: " + id + " не существует!"));
        schedule.setDeleted(true);
    }

    @Transactional
    public ScheduleDto update(Long id, ScheduleUpdateDto request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Расписания с id: " + id + " не существует!"));

        scheduleMapper.update(request, schedule);
        bindRelations(schedule, request.groupId(), request.teacherId(), request.courseId());

        return scheduleMapper.toDto(schedule);
    }

    private void bindRelations(Schedule schedule, Long groupId, Long teacherId, Long courseId) {
        schedule.setGroup(requireGroup(groupId));
        schedule.setTeacher(requireTeacher(teacherId));
        schedule.setCourse(requireCourse(courseId));
    }

    private Group requireGroup(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Группы с id: " + id + " не существует!"));
    }

    private Teacher requireTeacher(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Учителя с id: " + id + " не существует!"));
    }

    private Course requireCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Курса с id: " + id + " не существует!"));
    }
}