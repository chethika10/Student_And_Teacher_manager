package com.ISA.Student_And_Teacher_manager.service.impl;

import com.ISA.Student_And_Teacher_manager.repo.UserRepo;
import com.ISA.Student_And_Teacher_manager.service.UserService;
import com.ISA.Student_And_Teacher_manager.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;
    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepo.findAll();
    }

    @Override
    public User getUserById(int id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User addOrUpdateUser(User user) {
        return userRepo.save(user);
    }
}
