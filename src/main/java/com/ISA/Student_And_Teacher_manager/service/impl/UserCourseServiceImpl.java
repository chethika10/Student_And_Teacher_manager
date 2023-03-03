package com.ISA.Student_And_Teacher_manager.service.impl;

import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourse;
import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourseKey;
import com.ISA.Student_And_Teacher_manager.repo.UserCourseRepo;
import com.ISA.Student_And_Teacher_manager.service.UserCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCourseServiceImpl implements UserCourseService {
    @Autowired
    private UserCourseRepo userCourseRepo;
    @Override
    public UserCourse addUserToCourse(UserCourse userCourse) {
        return userCourseRepo.save(userCourse);
    }

    @Override
    public UserCourse deleteUserFromCourse(int userId, int courseId) {
        UserCourseKey key=new UserCourseKey();
        key.setCourseId(courseId);
        key.setUserId(userId);
        UserCourse deletedUsercourse= userCourseRepo.findById(key).orElse(null);
        if(deletedUsercourse!=null){
            userCourseRepo.deleteById(key);
        }
        return deletedUsercourse;
    }

    @Override
    public UserCourse updateUserCourse(UserCourse userCourse) {
        UserCourseKey key=userCourse.getUserCourseKey();

        if(key!=null && userCourseRepo.findById(key).orElse(null) !=null){
            return userCourseRepo.save(userCourse);
        }
        return null;
    }
}
