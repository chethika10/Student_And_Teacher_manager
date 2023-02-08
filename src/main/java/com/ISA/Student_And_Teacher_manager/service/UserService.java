package com.ISA.Student_And_Teacher_manager.service;

import com.ISA.Student_And_Teacher_manager.users.User;

import java.util.List;

public interface UserService {
    public List<User> getAllUsers();
    public User getUserById(int id);
    public User addOrUpdateUser(User user);
    public User getUserByUserName(String userName);
}
