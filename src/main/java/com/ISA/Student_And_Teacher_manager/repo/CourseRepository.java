package com.ISA.Student_And_Teacher_manager.repo;


import com.ISA.Student_And_Teacher_manager.entity.course.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<Course,Integer> {
}
