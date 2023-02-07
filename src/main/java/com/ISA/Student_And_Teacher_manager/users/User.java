package com.ISA.Student_And_Teacher_manager.users;

import java.time.LocalDate;

public class User {
    private int id;
    private String name;
    private int age;
    private LocalDate birthDay;
    private String userName;
    private String EMailAddress;

    public User() {
    }

    public User(int id, String name, int age, LocalDate birthDay, String userName, String EMailAddress) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.birthDay = birthDay;
        this.userName = userName;
        this.EMailAddress = EMailAddress;
    }

    public User(String name, int age, LocalDate birthDay, String userName, String EMailAddress) {
        this.name = name;
        this.age = age;
        this.birthDay = birthDay;
        this.userName = userName;
        this.EMailAddress = EMailAddress;
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
