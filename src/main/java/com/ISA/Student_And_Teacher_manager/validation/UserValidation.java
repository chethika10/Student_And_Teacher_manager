package com.ISA.Student_And_Teacher_manager.validation;

import com.ISA.Student_And_Teacher_manager.entity.users.User;
import com.ISA.Student_And_Teacher_manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

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
           // System.out.println(e.getMessage());
            //e.printStackTrace();
           // System.out.println(userService.getIdByUsername(user.getUserName()));
           // System.out.println(userService.checkEmail("sahan3@gmail.com"));
        }

        return "";
    }
}
