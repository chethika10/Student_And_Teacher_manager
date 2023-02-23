package com.ISA.Student_And_Teacher_manager.filter;

import com.ISA.Student_And_Teacher_manager.jwt.RefreshToken;
import com.ISA.Student_And_Teacher_manager.securityConfig.UserDetailsImpl;
import com.ISA.Student_And_Teacher_manager.service.TokenService;
import com.ISA.Student_And_Teacher_manager.service.impl.TokenServiceImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;



public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//    private  AuthenticationManager authenticationManager;
    @Autowired
    private TokenServiceImpl tokenService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
//        this.authenticationManager = authenticationManager;
//        this.tokenService=ctx.getBean(TokenServiceImpl.class);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String password;
        String userName;
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            userName = requestMap.get("username");
            password = requestMap.get("password");
            //System.out.println(userName + password);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        UsernamePasswordAuthenticationToken authenticationToken =new UsernamePasswordAuthenticationToken(userName,password);
        return this.getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl user=(UserDetailsImpl) authentication.getPrincipal();
        /* TODO change  the S&TManager with something encrypted*/
        Algorithm algorithm=Algorithm.HMAC256("S&TManager".getBytes());
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() +3*1*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("role",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() +60*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
//        response.setHeader("accessToken",accessToken);
//        response.setHeader("refreshToken",refreshToken);
        Map<String,String> tokens=new HashMap<>();
        tokens.put("accessToken",accessToken);
        tokens.put("refreshToken",refreshToken);
        RefreshToken refreshToken1=new RefreshToken();
        refreshToken1.setToken(refreshToken);
        refreshToken1.setUsername(user.getUsername());
        try {
        //    System.out.println("add1");
//            tokenService = new TokenServiceImpl();
            tokenService.addOrUpdateToken(refreshToken1);
        }catch (Exception e){
           // System.out.println("cds");
            e.printStackTrace();
        }

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),tokens);

    }
}
