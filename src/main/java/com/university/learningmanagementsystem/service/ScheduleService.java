package com.university.learningmanagementsystem.service;

import com.university.learningmanagementsystem.dto.PageResponse;
import com.university.learningmanagementsystem.dto.schedule.ScheduleCreateDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleDto;
import com.university.learningmanagementsystem.dto.schedule.ScheduleUpdateDto;
import com.university.learningmanagementsystem.entity.Schedule;
import com.university.learningmanagementsystem.mapper.ScheduleMapper;
import com.university.learningmanagementsystem.repository.CourseRepository;
import com.university.learningmanagementsystem.repository.GroupRepository;
import com.university.learningmanagementsystem.repository.ScheduleRepository;
import com.university.learningmanagementsystem.repository.TeacherRepository;
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
    public PageResponse<ScheduleDto> findAll(Pageable pageable) {
        Page<ScheduleDto> page = scheduleRepository.findAll(pageable).map(scheduleMapper::toDto);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public ScheduleDto findById(Long id) {
        return scheduleMapper.toDto(scheduleRepository.requireById(id));
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
        Schedule schedule = scheduleRepository.requireById(id);
        schedule.setDeleted(true);
    }

    @Transactional
    public ScheduleDto update(Long id, ScheduleUpdateDto request) {
        Schedule schedule = scheduleRepository.requireById(id);

        scheduleMapper.mapInto(request, schedule);
        bindRelations(schedule, request.groupId(), request.teacherId(), request.courseId());

        return scheduleMapper.toDto(schedule);
    }

    private void bindRelations(Schedule schedule, Long groupId, Long teacherId, Long courseId) {
        schedule.setGroup(groupRepository.requireById(groupId));
        schedule.setTeacher(teacherRepository.requireById(teacherId));
        schedule.setCourse(courseRepository.requireById(courseId));
    }
}