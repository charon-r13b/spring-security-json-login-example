package com.github.charon.r13b.spring.config;

import javax.servlet.http.HttpServletResponse;

import com.github.charon.r13b.spring.security.JsonUsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CSRF token off
        http
                .csrf().disable();

        // access control
        http
                .authorizeRequests()
                .mvcMatchers("/secure/user").hasAnyRole("USER", "ADMIN")
                .mvcMatchers("/secure/admin").hasAnyRole("ADMIN")
                .mvcMatchers("/secure/me").authenticated()
                .anyRequest().permitAll();

        // login settings
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter =
                new JsonUsernamePasswordAuthenticationFilter(authenticationManager());
        jsonUsernamePasswordAuthenticationFilter.setUsernameParameter("email");
        jsonUsernamePasswordAuthenticationFilter.setPasswordParameter("password");
        jsonUsernamePasswordAuthenticationFilter
                .setAuthenticationSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK));
        jsonUsernamePasswordAuthenticationFilter
                .setAuthenticationFailureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED));

        http.addFilterAt(jsonUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // access failure handling
        http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.exceptionHandling().accessDeniedHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_FORBIDDEN));

        // logout
        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
                .invalidateHttpSession(true);
    }
}
