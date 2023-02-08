package com.ISA.Student_And_Teacher_manager.users;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;
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

    public User() {
    }

    public User(int id, String name, int age, LocalDate birthDay, String userName, String EMailAddress, float salary, String role, String password) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.birthDay = birthDay;
        this.userName = userName;
        this.EMailAddress = EMailAddress;
        this.salary=salary;
        this.role=role;
        BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
        this.password=encoder.encode(password);
    }

    public User(String name, int age, LocalDate birthDay, String userName, String EMailAddress, float salary, String role, String password) {
        this.name = name;
        this.age = age;
        this.birthDay = birthDay;
        this.userName = userName;
        this.EMailAddress = EMailAddress;
        this.salary=salary;
        this.role=role;
        BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
        this.password=encoder.encode(password);

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
        this.password=encoder.encode(password);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEMailAddress() {
        return EMailAddress;
    }

    public void setEMailAddress(String EMailAddress) {
        this.EMailAddress = EMailAddress;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", birthDay=" + birthDay +
                ", userName='" + userName + '\'' +
                ", EMailAddress='" + EMailAddress + '\'' +
                '}';
    }
}
