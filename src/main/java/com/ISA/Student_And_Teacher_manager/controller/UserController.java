package com.ISA.Student_And_Teacher_manager.controller;

import com.ISA.Student_And_Teacher_manager.service.UserService;
import com.ISA.Student_And_Teacher_manager.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/STManager")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users= null;
        try {
            users=userService.getAllUsers();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);

    }
    @GetMapping("/getById/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int userId){
        User user= null;
        try {
            user=userService.getUserById(userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);

    }
    @PostMapping("/addOrUpdate")
    public ResponseEntity<User> addOrUpdate(@RequestBody User user){

        try {
            user=userService.addOrUpdateUser(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);

    }
}
