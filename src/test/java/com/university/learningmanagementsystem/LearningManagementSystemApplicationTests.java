package com.university.learningmanagementsystem;

import com.university.learningmanagementsystem.controller.CourseController;
import com.university.learningmanagementsystem.controller.GroupController;
import com.university.learningmanagementsystem.controller.ScheduleController;
import com.university.learningmanagementsystem.controller.StudentController;
import com.university.learningmanagementsystem.controller.TeacherController;
import com.university.learningmanagementsystem.service.CourseService;
import com.university.learningmanagementsystem.service.GroupService;
import com.university.learningmanagementsystem.service.ScheduleService;
import com.university.learningmanagementsystem.service.StudentService;
import com.university.learningmanagementsystem.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LearningManagementSystemApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private TeacherController teacherController;

	@Autowired
	private StudentController studentController;

	@Autowired
	private GroupController groupController;

	@Autowired
	private CourseController courseController;

	@Autowired
	private ScheduleController scheduleController;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private StudentService studentService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private ScheduleService scheduleService;

	@Test
	void contextLoads() {
		assertThat(applicationContext).isNotNull();
		assertThat(teacherController).isNotNull();
		assertThat(studentController).isNotNull();
		assertThat(groupController).isNotNull();
		assertThat(courseController).isNotNull();
		assertThat(scheduleController).isNotNull();
		assertThat(teacherService).isNotNull();
		assertThat(studentService).isNotNull();
		assertThat(groupService).isNotNull();
		assertThat(courseService).isNotNull();
		assertThat(scheduleService).isNotNull();
	}
}