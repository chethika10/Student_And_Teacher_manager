package com.ISA.Student_And_Teacher_manager.repo;


import com.ISA.Student_And_Teacher_manager.entity.course.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepo extends CrudRepository<Course,Integer> {
    @Query(value = "SELECT course_id,course_name,module_code,enabled FROM course",nativeQuery = true)
    public List<Object> getAllCourses();
}
