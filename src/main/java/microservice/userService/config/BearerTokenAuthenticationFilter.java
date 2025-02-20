package microservice.userService.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

// Authentication Filter
@Component
class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private BearerTokenAuthenticationProvider authenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

            BearerTokenAuthentication authRequest = new BearerTokenAuthentication(token);

            Authentication authenticationResult = null;

            try {
                authenticationResult = authenticationProvider.authenticate(authRequest);
            } catch (AuthenticationException e) {
                // Handle authentication failure (e.g., log, return error response)
                SecurityContextHolder.clearContext();
                throw e;
            }

            if (authenticationResult != null) {
                SecurityContextHolder.getContext().setAuthentication(authenticationResult);
            }
        }

        filterChain.doFilter(request, response);
    }
}
