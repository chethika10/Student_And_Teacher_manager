package com.ISA.Student_And_Teacher_manager.service;

import com.ISA.Student_And_Teacher_manager.entity.course.Course;

import java.util.List;

public interface CourseService {
    public Course addOrUpdateCourse(Course course);
    public Course getCourseById(int courseId);
    public List<Course> getAllCourses();
    public Course deleteCourseById(int courseId);
}
