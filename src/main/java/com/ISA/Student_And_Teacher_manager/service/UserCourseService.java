package com.ISA.Student_And_Teacher_manager.service;

import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourse;
import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourseKey;

import java.util.List;

public interface UserCourseService {
    public UserCourse addUserToCourse(UserCourse userCourse);
    public UserCourse deleteUserFromCourse(int userId, int courseId);
    public UserCourse updateUserCourse(UserCourse userCourse);
    public List<Object> getUsersForCourse(int courseId, String role);
    public boolean existsById(UserCourseKey key);
    public List<Object> getCoursesForUserId(int userId);
}
