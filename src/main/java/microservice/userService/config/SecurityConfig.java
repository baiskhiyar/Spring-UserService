package microservice.userService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable method-level security
public class SecurityConfig {

    @Autowired
    private BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;

    @Autowired
    private BearerTokenAuthenticationProvider bearerTokenAuthenticationProvider;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(bearerTokenAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(
                new WebExpressionVoter(), // Optional:  If you want to use Spring EL expressions
                new ScopeVoter(requestMappingHandlerMapping)
        );
        return new AffirmativeBased(decisionVoters); // Grant access if any voter grants it
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //  no sessions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/spring/users/register").permitAll() // Example:  allow public access
                        .requestMatchers("/spring/users/login").permitAll()
                        .anyRequest().authenticated() // Requires authentication for all other requests
                )
                .addFilterBefore(bearerTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add filter
        return http.build();
    }
}