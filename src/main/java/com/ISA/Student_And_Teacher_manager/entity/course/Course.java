package com.ISA.Student_And_Teacher_manager.entity.course;

import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "course")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int courseId;
    private String courseName;
    private float fee;

    private String moduleCode;
    private boolean enabled;
    private boolean started;
    private LocalDate startDate;
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserCourse> userCourses;
}
