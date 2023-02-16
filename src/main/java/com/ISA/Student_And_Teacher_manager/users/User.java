package com.ISA.Student_And_Teacher_manager.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private String EMailAddress;
    @Column(name = "salary")
    private float salary;
    @Column(name = "role")
    private String role;
    @Column(name = "password")
    private String password;
    @Column(name = "enabled")
    private boolean enabled;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
        this.password=encoder.encode(password);
    }


}
