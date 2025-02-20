package microservice.userService.config;

import microservice.userService.models.Users;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

// Custom Authentication Token
class BearerTokenAuthentication extends AbstractAuthenticationToken {
    private final String token;
    private Users user;

    public BearerTokenAuthentication(String token) {
        super(null);
        this.token = token;
        setAuthenticated(false); // Initially not authenticated
    }

    public BearerTokenAuthentication(String token, Users user, List<SimpleGrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.user = user;
        setAuthenticated(true); // Authenticated
    }


    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }

    public String getToken() {
        return token;
    }
}