package com.ISA.Student_And_Teacher_manager.controller;

import com.ISA.Student_And_Teacher_manager.entity.jwt.RefreshToken;
import com.ISA.Student_And_Teacher_manager.service.TokenService;
import com.ISA.Student_And_Teacher_manager.service.UserService;
import com.ISA.Student_And_Teacher_manager.entity.users.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/stmanager")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @GetMapping("/getall")
    public ResponseEntity<List<Object>> getAllUsers(){
        List<Object> users= null;
        try {
            users=userService.getAllUsers();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Object>>(users, HttpStatus.OK);

    }
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int userId){
        User user= null;
        try {
            user=userService.getUserById(userId);
            user.setPassword("");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);

    }
    @GetMapping("/getbyusername/{userName}")
    public ResponseEntity<User> getUserByUserName(@PathVariable("userName") String userName){
        User user= null;
        try {
            user=userService.getUserByUserName(userName);
            user.setPassword("");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);

    }
    @PostMapping("/register")
    public ResponseEntity<Integer> addOrUpdate(@RequestBody User user){
        if (user.getId() == 0 &&( user.getRole().equals("ADMIN")||user.getRole().equals("TEACHER"))){
            try {
                user.setEnabled(false);
                user=userService.addOrUpdateUser(user);
            }catch (Exception e){
                e.printStackTrace();
            }
            return new ResponseEntity<Integer>(user.getId(), HttpStatus.OK);

        }else if(user.getId() == 0 && user.getRole().equals("STUDENT")){

            try {
                user.setEnabled(true);
                user = userService.addOrUpdateUser(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Integer>(user.getId(), HttpStatus.OK);
        }
        return null;

    }
    @GetMapping("/refreshtoken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader=request.getHeader(AUTHORIZATION);

        if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")){
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                if(tokenService.getRefreshTokenByToken(refreshToken)==null){
                    throw new RuntimeException("User is logged out");
                }
                /* TODO change  the S&TManager with something encrypted*/
                Algorithm algorithm = Algorithm.HMAC256("S&TManager".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username=decodedJWT.getSubject();
                User user =userService.getUserByUserName(username);
                String accessToken = JWT.create()
                        .withSubject(user.getUserName())
                        .withExpiresAt(new Date(System.currentTimeMillis() +3*1*1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("role",(Arrays.asList(new SimpleGrantedAuthority(user.getRole()))).stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String,String> tokens=new HashMap<>();
                tokens.put("accessToken",accessToken);
                tokens.put("refreshToken",refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);
            }catch (TokenExpiredException e){
                System.out.println("TokenExpiredException");
                e.printStackTrace();
                response.setHeader("error",e.getMessage());

                response.setStatus(FORBIDDEN.value());
                Map<String,String> error=new HashMap<>();
                error.put("errorMessage","RefreshTokenExpiredException");
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }
            catch (Exception e){
                e.printStackTrace();
                response.setHeader("error",e.getMessage());
                response.setStatus(FORBIDDEN.value());
                //response.sendError(FORBIDDEN.value());
                Map<String,String> error=new HashMap<>();
                error.put("errorMessage",e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }
        }
        else {
            throw new RuntimeException("Refresh token not found");
        }
    }
    @GetMapping("/logout")
    public void logout( HttpServletResponse response, Principal principal) throws IOException {

            try {

                String username = principal.getName();
                RefreshToken refreshToken=tokenService.getRefreshTokenByUsername(username);
                tokenService.deleteToken(refreshToken);
            }catch (Exception e){
                e.printStackTrace();
                response.setHeader("error",e.getMessage());
                response.setStatus(FORBIDDEN.value());
                //response.sendError(FORBIDDEN.value());
                Map<String,String> error=new HashMap<>();
                error.put("errorMessage",e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }
 //   }
    }
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Principal principal){
        User user= null;
        try {
            user=userService.getUserByUserName(principal.getName());
            user.setPassword("");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
    @GetMapping("/enableuser/{userId}")
    public ResponseEntity<User> enableUser(@PathVariable("userId") int userId){
        User user=null;

        try{
            user=userService.getUserById(userId);
            if(user!=null) {
                user.setEnabled(true);
                userService.addOrUpdateUser(user);
                user.setPassword("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }
    @GetMapping("/disableuser/{userId}")
    public ResponseEntity<User> disableUser(@PathVariable("userId") int userId){
        User user=null;
        try{
            user=userService.getUserById(userId);
            if(user!=null) {
                user.setEnabled(false);
                userService.addOrUpdateUser(user);
                user.setPassword("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }
    @GetMapping("/deleteuser/{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable("userId") int userId){
        User user=null;
        try{
            user=userService.getUserById(userId);
            if(user!=null) {
                user=userService.deleteUserById(userId);
                user.setPassword("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }
}
