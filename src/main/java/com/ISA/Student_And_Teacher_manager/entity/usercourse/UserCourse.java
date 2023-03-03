package com.ISA.Student_And_Teacher_manager.entity.usercourse;

import com.ISA.Student_And_Teacher_manager.entity.course.Course;
import com.ISA.Student_And_Teacher_manager.entity.users.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user_course")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCourse {
    @EmbeddedId
    private UserCourseKey userCourseKey;
    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;
    private float marks;
}
