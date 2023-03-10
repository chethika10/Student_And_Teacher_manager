package com.ISA.Student_And_Teacher_manager.service.impl;

import com.ISA.Student_And_Teacher_manager.repo.UserRepo;
import com.ISA.Student_And_Teacher_manager.service.UserService;
import com.ISA.Student_And_Teacher_manager.entity.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;
    @Override
    public List<Object> getAllUsers() {
        return (List<Object>) userRepo.getAllUsers();
    }

    @Override
    public User getUserById(int id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User addOrUpdateUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public User getUserByUserName(String userName) {
        return userRepo.findUserByUserName(userName);
    }

    @Override
    public User deleteUserById(int id) {
        User user=userRepo.findById(id).orElse(null);
        if(user!=null){
            userRepo.deleteById(id);
        }
        return user;
    }

    @Override
    public int getIdByUsername(String username) {
        return userRepo.findIdByUserName(username);
    }

    @Override
    public int getIdByEmail(String email) {
        return userRepo.findIdByEmail(email);
    }
}
