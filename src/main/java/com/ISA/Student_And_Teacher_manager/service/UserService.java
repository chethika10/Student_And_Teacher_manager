package com.ISA.Student_And_Teacher_manager.service;

import com.ISA.Student_And_Teacher_manager.entity.users.User;

import java.util.List;

public interface UserService {
    public List<Object> getAllUsers();
    public User getUserById(int id);
    public User addOrUpdateUser(User user);
    public User getUserByUserName(String userName);
    public User deleteUserById(int id);
    public int getIdByUsername(String username);
    public int getIdByEmail(String email);
}
