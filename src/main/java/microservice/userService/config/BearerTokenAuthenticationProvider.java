package microservice.userService.config;

import microservice.userService.models.AccessTokenProvider;
import microservice.userService.models.RoleScopes;
import microservice.userService.models.UserRoles;
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
import java.util.List;
import java.util.stream.Collectors;

// Authentication Provider
@Component
class BearerTokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AccessTokenProviderRepository accessTokenProviderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRolesRepository userRoleRepository;

    @Autowired
    private RolesRepository roleRepository;

    @Autowired
    private RoleScopesRepository roleScopesRepository;

    @Autowired
    private ScopesRepository scopeRepository;


    @Override
    @Transactional(readOnly = true) // Important for lazy loading issues
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        BearerTokenAuthentication auth = (BearerTokenAuthentication) authentication;
        String token = auth.getToken();

        AccessTokenProvider accessTokenProvider = accessTokenProviderRepository.findByAccessToken(token);
        if (accessTokenProvider == null) {
            return null;  // Or throw an AuthenticationException
        }

        if (accessTokenProvider.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null; // Or throw an AuthenticationException
        }

        Users user = userRepository.findById(accessTokenProvider.getUserId());
        if (user == null) {
            return null;  // Or throw an AuthenticationException
        }

        // Fetch roles and scopes explicitly
        List<UserRoles> userRoles = userRoleRepository.findByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = userRoles.stream()
                .map(UserRoles::getRoleId)
                .map(roleId -> roleRepository.findById(roleId).orElse(null))
                .filter(role -> role != null)
                .flatMap(role -> {
                    List<RoleScopes> roleScopes = roleScopesRepository.findByRoleId(role.getId());
                    return roleScopes.stream()
                            .map(RoleScopes::getScopeId)
                            .map(scopeId -> scopeRepository.findById(Long.valueOf(scopeId)).orElse(null))
                            .filter(scope -> scope != null)
                            .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope.getName()));
                })
                .collect(Collectors.toList());

        return new BearerTokenAuthentication(token, user, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthentication.class.isAssignableFrom(authentication);
    }
}
