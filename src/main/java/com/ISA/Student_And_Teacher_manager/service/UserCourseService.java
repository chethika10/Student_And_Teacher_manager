package com.ISA.Student_And_Teacher_manager.service;

import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourse;

public interface UserCourseService {
    public UserCourse addUserToCourse(UserCourse userCourse);
    public UserCourse deleteUserFromCourse(int userId, int courseId);
    public UserCourse updateUserCourse(UserCourse userCourse);
}
