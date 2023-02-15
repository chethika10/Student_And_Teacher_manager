package com.ISA.Student_And_Teacher_manager.service.impl;

import com.ISA.Student_And_Teacher_manager.securityConfig.UserDetailsImpl;
import com.ISA.Student_And_Teacher_manager.repo.UserRepo;
import com.ISA.Student_And_Teacher_manager.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepo.findUserByUserName(username);
        if (user==null){
            throw new UsernameNotFoundException("Can not find user");
        }
        return new UserDetailsImpl(user);
    }
}
