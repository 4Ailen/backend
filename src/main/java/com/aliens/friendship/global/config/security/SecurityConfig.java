package com.aliens.friendship.global.config.security;

import com.aliens.friendship.domain.auth.filter.JwtAuthenticationFilter;
import com.aliens.friendship.domain.auth.filter.JwtEntryPoint;
import com.aliens.friendship.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final AuthService authService;
    private final JwtEntryPoint jwtEntryPoint;

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/ws-stomp/**", "/send/**", "/room/**").permitAll()
                .antMatchers(HttpMethod.POST, "/", "/join/**", "/login", "/api/v1/auth/authentication", "/api/v1/member", "/api/v1/auth/reissue", "/api/v1/member/{email}/password/temp", "/api/v1/email/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/email/{email}/existence", "/api/v1/member/{email}/authentication-status", "/api/v1/member/nationalities", "/api/v1/email/**").permitAll()
                .antMatchers("/matching/**", "/health", "/logout","/api/v1/chat/**").authenticated()
                .antMatchers("/images/character.png").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v1/member/{memberId}").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/member/{memberId}").hasRole("ADMIN")
                .anyRequest().hasRole("USER")

                .and()
                .exceptionHandling().

                and()
                .logout()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).

                and()// Add a filter to validate the tokens with every request
                .addFilterBefore(new JwtAuthenticationFilter(authService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtEntryPoint);

//        // 필터 해제
//        http.authorizeRequests()
//                .antMatchers("/**").permitAll()
//                .and().csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//
//
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/h2-console/**", "/favicon.ico");
    }
}