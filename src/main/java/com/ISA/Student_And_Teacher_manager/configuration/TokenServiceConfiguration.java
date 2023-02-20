package com.ISA.Student_And_Teacher_manager.configuration;

import com.ISA.Student_And_Teacher_manager.service.TokenService;
import com.ISA.Student_And_Teacher_manager.service.impl.TokenServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class TokenServiceConfiguration {

    @Bean
    TokenService tokenService(){
        return new TokenServiceImpl();
    }
}
