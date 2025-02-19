package microservice.userService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // Will fix all these things
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/spring/users/**").permitAll() // Permit all requests under /spring/users/
                        .anyRequest().authenticated() // All other requests require authentication
                );
        return http.build();
    }
}