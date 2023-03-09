package com.ISA.Student_And_Teacher_manager.entity.users;

import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name="user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
//    @Column(name = "age")
//    private int age;
    @Column(name="birthday")
    private LocalDate birthDay;
    @Column(name = "username")
    private String userName;
    @Column(name = "email")
    private String email;
    @Column(name = "salary")
    private float salary;
    @Column(name = "role")
    private String role;
    @Column(name = "password")
    private String password;
    @Column(name = "enabled")
    private boolean enabled;
    @Transient
    private String rowPassword;
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserCourse> userCourses;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
        this.password=encoder.encode(password);
    }


}
