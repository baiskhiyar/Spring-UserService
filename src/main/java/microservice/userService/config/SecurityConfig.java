package microservice.userService.config;

import microservice.userService.helpers.AuthenticationScopeChecker;
import microservice.userService.helpers.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private AuthenticationScopeChecker scopeVoter;

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disabling csrf
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/spring/users/login").permitAll().anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .build();
    }
}