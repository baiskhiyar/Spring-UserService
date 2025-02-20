package microservice.userService.config;

import jakarta.servlet.http.HttpServletRequest;
import microservice.userService.helpers.ScopesHelper;
import microservice.userService.models.Users;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Component
public class AuthenticationScopeChecker implements AccessDecisionVoter<Object> {

    @Qualifier("customHandlerMapping")
    private final HandlerMapping handlerMapping;

    public AuthenticationScopeChecker(HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return ACCESS_DENIED;
        }
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        HandlerMethod handlerMethod = null;
        try {
            handlerMethod = (HandlerMethod) Objects.requireNonNull(handlerMapping.getHandler(request)).getHandler();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        RequiredScopes requiredScopesAnnotation = handlerMethod.getMethodAnnotation(RequiredScopes.class);
        if (requiredScopesAnnotation == null) {
            return ACCESS_GRANTED; // No scopes required, allow access
        }
        String[] requiredScopes = Set.of(requiredScopesAnnotation.value()).toArray(new String[0]);
        Users user = (Users) authentication.getDetails();
        String[] availableScopes = user.getAvailableScopes();
        if (!ScopesHelper.hasRequiredScopes(availableScopes, requiredScopes)) {
            return ACCESS_DENIED;
        }
        return ACCESS_GRANTED;
    }
}

