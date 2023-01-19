package com.example.pureoauth2gradle.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .cors().disable()
                .authorizeHttpRequests()
                .requestMatchers("/login", "/login*", "/login/oauth2/code/github", "/oauth2/**","/token").permitAll()
                .anyRequest().permitAll() //.authenticated()
                .and()
                .formLogin().loginPage("/loginPage").successForwardUrl("/")
                .and()
                .build();
    }

//    private static List<String> clients = Arrays.asList("google", "facebook");
//
//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        List<ClientRegistration> registrations = clients.stream()
//                .map(c -> getRegistration(c))
//                .filter(registration -> registration != null)
//                .collect(Collectors.toList());
//
//        return new InMemoryClientRegistrationRepository(registrations);
//    }

//    public InMemoryUserDetailsManager inMemoryUserDetailsManager(AuthenticationManager authenticationManager) {
//        authenticationManager
//    }

}
