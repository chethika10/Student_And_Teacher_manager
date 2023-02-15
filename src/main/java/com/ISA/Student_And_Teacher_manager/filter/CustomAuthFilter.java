package com.ISA.Student_And_Teacher_manager.filter;

import com.ISA.Student_And_Teacher_manager.securityConfig.UserDetailsImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

public class CustomAuthFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public CustomAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String userName=request.getParameter("userName");
        String password=request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken =new UsernamePasswordAuthenticationToken(userName,password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl user=(UserDetailsImpl) authentication.getPrincipal();
        /* TODO change  the S&TManager with something encrypted*/
        Algorithm algorithm=Algorithm.HMAC256("S&TManager".getBytes());
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() +30*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("role",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() +60*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        response.setHeader("accessToken",accessToken);
        response.setHeader("refreshToken",refreshToken);

    }
}
