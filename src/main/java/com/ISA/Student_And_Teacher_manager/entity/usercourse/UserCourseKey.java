package com.ISA.Student_And_Teacher_manager.entity.usercourse;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Embeddable
public class UserCourseKey implements Serializable {
    @Column(name = "user_id")
    private int userId;
    @Column(name = "course_id")
    private int courseId;
}
