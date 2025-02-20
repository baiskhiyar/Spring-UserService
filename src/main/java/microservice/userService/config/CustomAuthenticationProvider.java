package microservice.userService.config;

import microservice.userService.helpers.ScopesHelper;
import microservice.userService.helpers.TimeUtility;
import microservice.userService.models.AccessTokenProvider;
import microservice.userService.models.Users;
import microservice.userService.repository.AccessTokenProviderRepository;
import microservice.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AccessTokenProviderRepository accessTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("inside CustomAuthenticationProvider authenticate");
        String token = (String) authentication.getCredentials();

        AccessTokenProvider accessToken = accessTokenRepository.findByAccessToken(token);
        if (accessToken == null) {
            throw new RuntimeException("Invalid access token");
        }
        if (accessToken.getExpiresAt().isBefore(TimeUtility.getCurrentDateTime())) {
            accessTokenRepository.delete(accessToken);
            throw new RuntimeException("Token expired");
        }
        Users user = userRepository.findById(accessToken.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setAvailableScopes(ScopesHelper.parseScopes(accessToken.getAvailableScopes()));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, user.getAuthorities()
        );
        auth.setDetails(user);
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}