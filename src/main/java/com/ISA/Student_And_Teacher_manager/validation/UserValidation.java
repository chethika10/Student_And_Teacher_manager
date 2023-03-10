package com.ISA.Student_And_Teacher_manager.validation;

import com.ISA.Student_And_Teacher_manager.entity.users.User;
import com.ISA.Student_And_Teacher_manager.service.UserService;

import java.time.LocalDate;

public class UserValidation {


    private UserService userService;

    public UserValidation(UserService userService) {
        this.userService = userService;
    }

    public String validateUser(User user){
        String regex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        if(user.getName().length()==0 || !user.getName().matches(regex)){
            //System.out.println(user.getName().matches(regex));
            return "Name is not valid";
        }


        if (user.getBirthDay()==null || (LocalDate.now().getYear()-user.getBirthDay().getYear())<1){
            return "Birthday is not valid";
        }
        try{
        if(user.getUserName().equals("") || userService.getIdByUsername(user.getUserName())!=0){
            return "Username is already taken";
        }
        }catch (Exception e){
           // System.out.println(userService.getUserByUserName(user.getUserName()));
         //   System.out.println(e.getMessage());
        }
        try{
            if(user.getEmail().equals("") || userService.getIdByEmail(user.getEmail())!=0){
                return "Email is already taken";
            }
        }catch (Exception e){
        }

        return "";
    }
    public String validateUsername(String newUsername,String username){
        if(newUsername.equalsIgnoreCase(username)){
            return "Your new username is same as old username";
        }
        try {
            if (newUsername.equals("") || userService.getIdByUsername(newUsername) != 0) {
                return "Username is already taken";
            }
        }catch (Exception e){

        }
        return "";
    }
    public String validateEditedAccount(User editedUser,User oldUser){
        String regex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        if((!oldUser.getName().equals(editedUser.getName())) && (editedUser.getName().length()==0 || !editedUser.getName().matches(regex))){
            //System.out.println(user.getName().matches(regex));
            return "Name is not valid";
        }
        if( (!oldUser.getBirthDay().equals(editedUser.getBirthDay())) && (editedUser.getBirthDay()==null || (LocalDate.now().getYear()-editedUser.getBirthDay().getYear())<1)){
            return "Birthday is not valid";
        }
        try{
            if( (!oldUser.getEmail().equals(editedUser.getEmail())) &&(editedUser.getEmail().equals("") || userService.getIdByEmail(editedUser.getEmail())!=0)){
                return "Email is already taken";
            }
        }catch (Exception e){
        }
        return "";
    }
}
