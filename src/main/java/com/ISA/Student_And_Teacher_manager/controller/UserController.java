package com.ISA.Student_And_Teacher_manager.controller;

import com.ISA.Student_And_Teacher_manager.service.UserService;
import com.ISA.Student_And_Teacher_manager.users.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
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

    @GetMapping("/getall")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users= null;
        try {
            users=userService.getAllUsers();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);

    }
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int userId){
        User user= null;
        try {
            user=userService.getUserById(userId);
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);

    }
    @PostMapping("/addorupdate")
    public ResponseEntity<Integer> addOrUpdate(@RequestBody User user){
        if (user.getId() != 0 || !user.getRole().equals("STUDENT")){
            return null;

        }

        try {
            user=userService.addOrUpdateUser(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<Integer>(user.getId(), HttpStatus.OK);

    }
    @GetMapping("/refreshtoken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader=request.getHeader(AUTHORIZATION);

        if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")){
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                /* TODO change  the S&TManager with something encrypted*/
                Algorithm algorithm = Algorithm.HMAC256("S&TManager".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username=decodedJWT.getSubject();
                User user =userService.getUserByUserName(username);
                String accessToken = JWT.create()
                        .withSubject(user.getUserName())
                        .withExpiresAt(new Date(System.currentTimeMillis() +30*60*1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("role",(Arrays.asList(new SimpleGrantedAuthority(user.getRole()))).stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String,String> tokens=new HashMap<>();
                tokens.put("accessToken",accessToken);
                tokens.put("refreshToken",refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);
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
}
