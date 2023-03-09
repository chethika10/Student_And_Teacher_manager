package com.ISA.Student_And_Teacher_manager.service.impl;

import com.ISA.Student_And_Teacher_manager.entity.course.Course;
import com.ISA.Student_And_Teacher_manager.repo.CourseRepo;
import com.ISA.Student_And_Teacher_manager.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepo courseRepo;
    @Override
    public Course addOrUpdateCourse(Course course) {
        return courseRepo.save(course);
    }

    @Override
    public Course getCourseById(int courseId) {
        return courseRepo.findById(courseId).orElse(null);
    }

    @Override
    public List<Object> getAllCourses() {
        return  (List<Object>) courseRepo.getAllCourses();
    }

    @Override
    public Course deleteCourseById(int courseId) {
        Course deletedCourse= courseRepo.findById(courseId).orElse(null);
        if(deletedCourse != null){
            courseRepo.deleteById(courseId);
        }
        return deletedCourse;
    }

    @Override
    public Course getCourseByModuleCode(String moduleCode) {
        return courseRepo.findCourseByModuleCode(moduleCode);
    }
}
