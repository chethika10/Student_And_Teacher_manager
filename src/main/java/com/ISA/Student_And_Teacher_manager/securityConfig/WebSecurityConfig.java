package com.ISA.Student_And_Teacher_manager.securityConfig;

import com.ISA.Student_And_Teacher_manager.filter.CustomAuthenticationFilter;
import com.ISA.Student_And_Teacher_manager.filter.CustomAuthorizationFilter;
import com.ISA.Student_And_Teacher_manager.service.impl.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceImpl();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter= CustomAuthenticationFilter();
        customAuthenticationFilter.setFilterProcessesUrl("/stmanager/login");
        http.csrf().disable().cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/stmanager/login/**" ,"/stmanager/refreshtoken/**","/stmanager/register/**","/stmanager/getallcources/**").permitAll();
        http.authorizeRequests()
                .antMatchers("/stmanager/getbyusername/**","/stmanager/getall/**" ,
                        "/stmanager/enableuser/**","/stmanager/disableuser/**","/stmanager/deleteuser/**",
                        "/stmanager/setSalary/**","/stmanager/addCourse/**","/stmanager/enablecourse/**","/stmanager/disablecourse/**",
                        "/stmanager/deletecourse/**","/stmanager/addteacher/**","/stmanager/removeteacher/**","/stmanager/unenrolstudent/**",
                        "/stmanager/addcoursetoteacher/**","/stmanager/removecoursefromteacher/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers("/stmanager/getstudents/**","/stmanager/getbyid/**","/stmanager/getcourses/**").hasAnyAuthority("ADMIN","TEACHER");
        http.authorizeRequests().antMatchers("/stmanager/start/**").hasAnyAuthority("TEACHER");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CustomAuthenticationFilter CustomAuthenticationFilter() throws Exception {
        return new CustomAuthenticationFilter(authenticationManagerBean());
    }
}
