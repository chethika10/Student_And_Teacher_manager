package com.ISA.Student_And_Teacher_manager.repo;

import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourse;
import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourseKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseRepo extends CrudRepository<UserCourse, UserCourseKey> {
}
