package com.ISA.Student_And_Teacher_manager.service.impl;

import com.ISA.Student_And_Teacher_manager.entity.course.Course;
import com.ISA.Student_And_Teacher_manager.repo.CourseRepository;
import com.ISA.Student_And_Teacher_manager.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImplementation implements CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Override
    public Course addOrUpdateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(int courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    @Override
    public List<Course> getAllCourses() {
        return (List<Course>) courseRepository.findAll();
    }

    @Override
    public Course deleteCourseById(int courseId) {
        Course deletedCourse=courseRepository.findById(courseId).orElse(null);
        if(deletedCourse != null){
            courseRepository.deleteById(courseId);
        }
        return deletedCourse;
    }
}
