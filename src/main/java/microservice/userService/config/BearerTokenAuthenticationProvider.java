package microservice.userService.config;

import microservice.userService.helpers.ScopesHelper;
import microservice.userService.models.AccessTokenProvider;
import microservice.userService.models.Users;
import microservice.userService.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

// Authentication Provider
@Component
class BearerTokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AccessTokenProviderRepository accessTokenProviderRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // Important for lazy loading issues
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        BearerTokenAuthentication auth = (BearerTokenAuthentication) authentication;
        String token = auth.getToken();
        // Getting accessTokenProvider from the bearer token.
        AccessTokenProvider accessTokenProvider = accessTokenProviderRepository.findByAccessToken(token);
        if (accessTokenProvider == null) {
            return null;  // Or throw an AuthenticationException
        }
        if (accessTokenProvider.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null; // Or throw an AuthenticationException
        }
        // Getting user from the authToken
        Users user = userRepository.findById(accessTokenProvider.getUserId());
        if (user == null) {
            return null;  // Or throw an AuthenticationException
        }
        // Adding users scopes into the authorities which will be used on api level @PreAuthorize
        String[] userScopes = ScopesHelper.parseScopes(accessTokenProvider.getAvailableScopes());
        List<SimpleGrantedAuthority> authorities = Arrays.stream(userScopes)
                .map(String::trim) // Trim whitespace, important!
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new BearerTokenAuthentication(token, user, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthentication.class.isAssignableFrom(authentication);
    }
}