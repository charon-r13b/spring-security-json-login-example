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
        // ログイン後にリダイレクトしないでいいので、AuthenticationSuccessHandlerを設定
        jsonUsernamePasswordAuthenticationFilter
                .setAuthenticationSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK));
        // ログイン失敗時にリダイレクトしないでいいので、AuthenticationFailureHandlerを設定
        jsonUsernamePasswordAuthenticationFilter
                .setAuthenticationFailureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED));

        // FormログインのFilterを置き換える
        http.addFilterAt(jsonUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // access failure handling
        // アクセス権限（ROLE）設定したページに、未認証状態でアクセスすると403を返すので、挙動を変更
        http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        // 403エラー時にHTTP Bodyを返すが、これは不要なのでAccessDeniedHandlerを設定
        http.exceptionHandling().accessDeniedHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_FORBIDDEN));

        // logout
        http
                .logout()
                .logoutUrl("/logout")
                // ログアウト時にリダイレクトしないでいいので、LogoutSuccessHandlerを設定
                .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
                .invalidateHttpSession(true);
    }
}
