package com.ISA.Student_And_Teacher_manager.repo;

import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourse;
import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourseKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCourseRepo extends CrudRepository<UserCourse, UserCourseKey> {

    @Query(value = "select id,name,username,enabled,role from user where id in (select user_id from user_course where course_id=?1) and role=?2",nativeQuery = true)
    public List<Object> getUsersForCourse(int courseId,String role);

    @Query(value = "select course_id,course_name,module_code,enabled,started from course where course_id in (select course_id from user_course where user_id=?1)" ,nativeQuery = true)
    public List<Object> getCoursesForUserId(int userId);
}
