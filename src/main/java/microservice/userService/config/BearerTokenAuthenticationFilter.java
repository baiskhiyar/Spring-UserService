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
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String bearerAuthToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
                BearerTokenAuthentication authRequest = new BearerTokenAuthentication(bearerAuthToken);
                // This method call is responsible for getting the user from the auth token and adding scopes in the
                // scope authority which will be used to check required scopes vs available scopes on the api level.
                Authentication authenticationResult = authenticationProvider.authenticate(authRequest);
                if (authenticationResult == null) {
                    System.out.println("Authentication Failed");
                } else {
                    // Setting authUser and scopes in the context which will be available throughout the api request
                    // journey.
                    SecurityContextHolder.getContext().setAuthentication(authenticationResult);
                }
                // TODO : Add else condition and log it.
            } catch (AuthenticationException e) {
                // Handle authentication failure (e.g., log, return error response)
                SecurityContextHolder.clearContext();
                throw e;
            }
        }
        // TODO : Add else condition where we will log that bearer token is missing in the request.
        filterChain.doFilter(request, response);
    }
}
