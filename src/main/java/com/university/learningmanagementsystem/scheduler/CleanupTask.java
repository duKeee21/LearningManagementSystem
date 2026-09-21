package com.university.learningmanagementsystem.scheduler;

import com.university.learningmanagementsystem.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanupTask {

    private final ScheduleRepository scheduleRepository;

    @Scheduled(cron = "${app.scheduler.cleanup.cron}")
    @Transactional
    public void deleteOldSchedules() {
        LocalDateTime cutoff = LocalDateTime.now().minusYears(1);
        int deleted = scheduleRepository.deleteByEndDateBefore(cutoff);
        System.out.println("Удалено устаревших записей: " + deleted);
    }
}