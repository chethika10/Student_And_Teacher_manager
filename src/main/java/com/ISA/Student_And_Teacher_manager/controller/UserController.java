package com.ISA.Student_And_Teacher_manager.controller;

import com.ISA.Student_And_Teacher_manager.DTO.NewStringAndPassword;
import com.ISA.Student_And_Teacher_manager.DTO.UserIdAndModuleCode;
import com.ISA.Student_And_Teacher_manager.DTO.UsernameAndCourse;
import com.ISA.Student_And_Teacher_manager.entity.course.Course;
import com.ISA.Student_And_Teacher_manager.entity.jwt.RefreshToken;
import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourse;
import com.ISA.Student_And_Teacher_manager.entity.usercourse.UserCourseKey;
import com.ISA.Student_And_Teacher_manager.service.CourseService;
import com.ISA.Student_And_Teacher_manager.service.TokenService;
import com.ISA.Student_And_Teacher_manager.service.UserCourseService;
import com.ISA.Student_And_Teacher_manager.service.UserService;
import com.ISA.Student_And_Teacher_manager.entity.users.User;
import com.ISA.Student_And_Teacher_manager.validation.UserValidation;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    private CourseService courseService;
    @Autowired
    private UserCourseService userCourseService;

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
    public ResponseEntity<User> getUserById(@PathVariable("id") int userId ,Principal principal){
        User user= new User();
        User user1=userService.getUserByUserName(principal.getName());
        try {
            user=userService.getUserById(userId);
            user.setPassword("");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(user.getRole().equals("STUDENT")) {
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } else if (user1.getRole().equals("ADMIN")) {
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }
        return new ResponseEntity<User>(user1, FORBIDDEN);


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
    public ResponseEntity<List<String>> addOrUpdate(@RequestBody User user){
       // System.out.println(user);
        UserValidation validation=new UserValidation(userService);
        String validationError=validation.validateUser(user);
        List<String> response=new ArrayList<>();
        if(!validationError.equals("")){
            response.add("0");
            response.add(validationError);
            return new ResponseEntity<List<String>>(response, HttpStatus.OK);
        }
        if (user.getId() == 0 &&( user.getRole().equals("ADMIN")||user.getRole().equals("TEACHER"))){
            try {
                user.setEnabled(false);
                user.setPassword(user.getRowPassword());
                user=userService.addOrUpdateUser(user);
            }catch (Exception e){
                e.printStackTrace();
            }

            response.add(""+user.getId());
            return new ResponseEntity<List<String>>(response, HttpStatus.OK);

        }else if(user.getId() == 0 && user.getRole().equals("STUDENT")){

            try {
                user.setEnabled(true);
                user.setPassword(user.getRowPassword());
                user = userService.addOrUpdateUser(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.add(""+user.getId());
            return new ResponseEntity<List<String>>(response, HttpStatus.OK);
        }
        response.add("0");
        response.add("No Role Selected");
        return new ResponseEntity<List<String>>(response, HttpStatus.OK);

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
                        .withExpiresAt(new Date(System.currentTimeMillis() +3*60*1000))
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
    @PostMapping("/setSalary")
    public ResponseEntity<User> setSalary(@RequestBody User user){
        User user1;
        try {
            user1=userService.getUserById(user.getId());
            if(user1.getRole()!="STUDENT") {
                user1.setSalary(user.getSalary());
                user = userService.addOrUpdateUser(user1);
            }
            user.setPassword("");
        }catch (Exception e){
            user=null;
            e.printStackTrace();
        }
        return  new ResponseEntity<User>(user,HttpStatus.OK);
    }
    @PostMapping("/addCourse")
    public ResponseEntity<Course> addCourse(@RequestBody Course course){
        try {
            if(course.getCourseId()==0){
                course.setEnabled(false);
                course.setStarted(false);
                course=courseService.addOrUpdateCourse(course);
            }
        }catch (Exception e){
            //course=null;
            e.printStackTrace();
        }
        return  new ResponseEntity<Course>(course,HttpStatus.OK);
    }
    @GetMapping("/getallcources")
    public ResponseEntity<List<Object>> getAllCourses(){
        List<Object> courses= null;
        try {
            courses=courseService.getAllCourses();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Object>>(courses, HttpStatus.OK);

    }

    @GetMapping("/enablecourse/{courseId}")
    public ResponseEntity<Course> enableCourse(@PathVariable("courseId") int courseId){
        Course course=null;

        try{
            course=courseService.getCourseById(courseId);
            if(course!=null) {
                course.setEnabled(true);
                courseService.addOrUpdateCourse(course);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<Course>(course,HttpStatus.OK);
    }
    @GetMapping("/disablecourse/{courseId}")
    public ResponseEntity<Course> disableCourse(@PathVariable("courseId") int courseId){
        Course course=null;
        try{
            course=courseService.getCourseById(courseId);
            if(course!=null) {
                course.setEnabled(false);
                courseService.addOrUpdateCourse(course);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<Course>(course,HttpStatus.OK);
    }
    @GetMapping("/deletecourse/{courseId}")
    public ResponseEntity<Course> deleteCourse(@PathVariable("courseId") int courseId){
        Course course=null;
        try{
            course=courseService.getCourseById(courseId);
            if(course!=null) {
                course=courseService.deleteCourseById(courseId);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<Course>(course,HttpStatus.OK);
    }

    @GetMapping("/getcoursebyid/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable("id") int courseId){
        Course course= null;
        try {
            course=courseService.getCourseById(courseId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<Course>(course, HttpStatus.OK);

    }
    @GetMapping("/getteachers/{id}")
    public ResponseEntity<List<Object>> getTeachersForCourse(@PathVariable("id") int courseId){
        List<Object> users= null;
        try {
            users=userCourseService.getUsersForCourse(courseId,"TEACHER");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Object>>(users, HttpStatus.OK);
    }
    @GetMapping("/getstudents/{id}")
    public ResponseEntity<List<Object>> getStudentsForCourse(@PathVariable("id") int courseId){
        List<Object> users= null;
        try {
            users=userCourseService.getUsersForCourse(courseId,"STUDENT");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Object>>(users, HttpStatus.OK);
    }
    @PostMapping("/addteacher")
    public ResponseEntity<Boolean> addTeacherToCourse(@RequestBody UsernameAndCourse usernameAndCourse){
         UserCourse userCourse = null;
        try {
            User user=userService.getUserByUserName(usernameAndCourse.getUsername());
            Course course=courseService.getCourseById(usernameAndCourse.getCourseId());
            userCourse=new UserCourse();
            UserCourseKey key=new UserCourseKey(user.getId(),course.getCourseId());
            userCourse.setUserCourseKey(key);
            if(!userCourseService.existsById(key)){
            userCourse.setCourse(course);
            userCourse.setUser(user);
            userCourse=userCourseService.addUserToCourse(userCourse);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }
            else {
                throw new RuntimeException("userCourse Already exists");
            }
        }catch (Exception e){
           // e.printStackTrace();
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/removeteacher")
    public ResponseEntity<Boolean> removeTeacherFromCourse(@RequestBody UsernameAndCourse usernameAndCourse){
        try {
            User user=userService.getUserByUserName(usernameAndCourse.getUsername());
            if(user.getRole().equals("TEACHER")) {
                int userId = user.getId();
                int courseId = usernameAndCourse.getCourseId();
                userCourseService.deleteUserFromCourse(userId, courseId);
            }else {
                throw new RuntimeException("user is not a teacher");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
    @GetMapping("/enrol/{id}")
    public ResponseEntity<Boolean> enrolForCourse(@PathVariable("id") int courseId,Principal principal){
        try {
            User user=userService.getUserByUserName(principal.getName());
            Course course=courseService.getCourseById(courseId);
            if(user.getRole().equals("STUDENT") && !course.isStarted() && course.isEnabled()) {

                int userId = user.getId();
                UserCourseKey key=new UserCourseKey(userId,courseId);
                UserCourse userCourse=new UserCourse();
                userCourse.setUserCourseKey(key);
                userCourse.setUser(user);
                userCourse.setCourse(course);
                userCourseService.addUserToCourse(userCourse);

            }else {
                throw new RuntimeException("user is not a student or course started");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
    @GetMapping("/unenrol/{id}")
    public ResponseEntity<Boolean> UnEnrolFromCourse(@PathVariable("id") int courseId,Principal principal){
        try {
            User user=userService.getUserByUserName(principal.getName());
            Course course=courseService.getCourseById(courseId);
            if(user.getRole().equals("STUDENT") && !course.isStarted() && course.isEnabled()) {

                int userId = user.getId();
                userCourseService.deleteUserFromCourse(userId,courseId);

            }else {
                throw new RuntimeException("user is not a student or course started");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> checkEnrolled(@PathVariable("id") int courseId, Principal principal){
        try {
            User user=userService.getUserByUserName(principal.getName());
            Course course=courseService.getCourseById(courseId);
            int userId = user.getId();
            UserCourseKey key=new UserCourseKey(userId,courseId);
            if( userCourseService.existsById(key)) {
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);


            }else {
                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/unenrolstudent")
    public ResponseEntity<Boolean> removeStudentFromCourse(@RequestBody UsernameAndCourse usernameAndCourse){
        try {
            User user=userService.getUserByUserName(usernameAndCourse.getUsername());
            if(user.getRole().equals("STUDENT")) {
                int userId = user.getId();
                int courseId = usernameAndCourse.getCourseId();
                userCourseService.deleteUserFromCourse(userId, courseId);
            }else {
                throw new RuntimeException("user is not a student");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
    @GetMapping("/started/{id}")
    public ResponseEntity<Boolean> checkStarted(@PathVariable("id") int courseId){
        try {
            Course course=courseService.getCourseById(courseId);
            if( course.isStarted()) {
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);


            }else {
                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/start/{id}")
    public ResponseEntity<Boolean> checkStarted(@PathVariable("id") int courseId,Principal principal){
        try {
            User user=userService.getUserByUserName(principal.getName());
            Course course=courseService.getCourseById(courseId);
            int userId = user.getId();
            UserCourseKey key=new UserCourseKey(userId,courseId);
            if( !course.isStarted() && userCourseService.existsById(key)) {
                course.setStarted(true);
                courseService.addOrUpdateCourse(course);
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);


            }else {
                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/getcourses/{id}")
    public ResponseEntity<List<Object>> getCoursesForUser(@PathVariable("id") int userId){
        List<Object> courses= null;
        try {
            courses=userCourseService.getCoursesForUserId(userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Object>>(courses, HttpStatus.OK);
    }
    @PostMapping("/addcoursetoteacher")
    public ResponseEntity<Boolean> addCourseToTeacher(@RequestBody UserIdAndModuleCode userIdAndModuleCode){
        UserCourse userCourse = null;
        try {
            User user=userService.getUserById(userIdAndModuleCode.getUserId());
            Course course=courseService.getCourseByModuleCode(userIdAndModuleCode.getModuleCode());
            userCourse=new UserCourse();
            UserCourseKey key=new UserCourseKey(user.getId(),course.getCourseId());
            userCourse.setUserCourseKey(key);
            if(!userCourseService.existsById(key)){
                userCourse.setCourse(course);
                userCourse.setUser(user);
                userCourse=userCourseService.addUserToCourse(userCourse);
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }
            else {
                throw new RuntimeException("userCourse Already exists");
            }
        }catch (Exception e){
            // e.printStackTrace();
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/removecoursefromteacher")
    public ResponseEntity<Boolean> removeCourseFromTeacher(@RequestBody UserIdAndModuleCode userIdAndModuleCode){
        try {
            User user=userService.getUserById(userIdAndModuleCode.getUserId());
            if(user.getRole().equals("TEACHER")) {
                int userId = user.getId();
                int courseId = courseService.getCourseByModuleCode(userIdAndModuleCode.getModuleCode()).getCourseId();
                userCourseService.deleteUserFromCourse(userId, courseId);
            }else {
                throw new RuntimeException("user is not a teacher");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
    @GetMapping("/getmycourses")
    public ResponseEntity<List<Object>> getMyCourses(Principal principal){
        List<Object> courses= null;
        try {
            courses=userCourseService.getCoursesForUserId(userService.getIdByUsername(principal.getName()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Object>>(courses, HttpStatus.OK);
    }

    @PostMapping("/changepassword")
    public ResponseEntity<List<String>> changePassword(@RequestBody NewStringAndPassword newStringAndPassword,HttpServletResponse response,Principal principal){
        List<String> err=new ArrayList<>();
        try {
            User user=userService.getUserByUserName(principal.getName());
            String encodedPassword=user.getPassword();
            BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
            if(encoder.matches(newStringAndPassword.getNewString(),encodedPassword)){
                err.add("0");
                err.add("Your Current password is same as old");
                return new ResponseEntity<List<String>>(err, HttpStatus.OK);
            }
            if(encoder.matches(newStringAndPassword.getPassword(),encodedPassword)){
                user.setPassword(newStringAndPassword.getNewString());
                userService.addOrUpdateUser(user);
                logout(response,principal);
                err.add("1");
                err.add("Successful");
            }else{
                err.add("0");
                err.add("Your Current password is wrong");
            }
            return new ResponseEntity<List<String>>(err, HttpStatus.OK);

        }catch (Exception e){
            System.out.println(e.getMessage());
            err.add("0");
            err.add("Something wrong");
            return new ResponseEntity<List<String>>(err, HttpStatus.OK);
        }
    }
    @PostMapping("/changeusername")
    public ResponseEntity<List<String>> changeUsername(@RequestBody NewStringAndPassword newStringAndPassword,HttpServletResponse response,Principal principal){
        List<String> err=new ArrayList<>();
        try {
            User user=userService.getUserByUserName(principal.getName());
            String encodedPassword=user.getPassword();
            BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
            UserValidation validation=new UserValidation(userService);
            String validationErr=validation.validateUsername(newStringAndPassword.getNewString(),principal.getName());
            if(!validationErr.equals("")){
                err.add("0");
                err.add(validationErr);
                return new ResponseEntity<List<String>>(err, HttpStatus.OK);
            }
            if(encoder.matches(newStringAndPassword.getPassword(),encodedPassword)){
                user.setUserName(newStringAndPassword.getNewString());
                userService.addOrUpdateUser(user);
                logout(response,principal);
                err.add("1");
                err.add("Successful");
            }else{
                err.add("0");
                err.add("Your Current password is wrong");
            }
            return new ResponseEntity<List<String>>(err, HttpStatus.OK);

        }catch (Exception e){
            System.out.println(e.getMessage());
            err.add("0");
            err.add("Something wrong");
            return new ResponseEntity<List<String>>(err, HttpStatus.OK);
        }
    }
    @PostMapping("/editaccount")
    public ResponseEntity<List<String>> editAccount(@RequestBody User user,Principal principal){
        List<String> err=new ArrayList<>();
        try {
            User user1 = userService.getUserByUserName(principal.getName());
            UserValidation validation=new UserValidation(userService);
            String validationErr=validation.validateEditedAccount(user,user1);
            if(!validationErr.equals("")){
                err.add("0");
                err.add(validationErr);
            }else {
                user1.setName(user.getName());
                user1.setBirthDay(user.getBirthDay());
                user1.setEmail(user.getEmail());
                userService.addOrUpdateUser(user1);
                err.add("1");
                err.add("Successful");
            }
            return new ResponseEntity<List<String>>(err, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            err.add("0");
            err.add("Something wrong");
            return new ResponseEntity<List<String>>(err, HttpStatus.OK);
        }
    }
}
