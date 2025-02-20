package microservice.userService.config;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class ScopeVoter implements AccessDecisionVoter<Object> {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public ScopeVoter(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true; // We support all attributes
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true; // We support all classes
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (!(object instanceof FilterInvocation)) {
            return ACCESS_ABSTAIN;
        }

        FilterInvocation filterInvocation = (FilterInvocation) object;

        try {
            HandlerExecutionChain handler = requestMappingHandlerMapping.getHandler(filterInvocation.getRequest());

            if (handler == null || handler.getHandler() == null) {
                return ACCESS_ABSTAIN; // No handler found, abstain
            }

            Object handlerObject = handler.getHandler();

            if (!(handlerObject instanceof HandlerMethod)) {
                return ACCESS_ABSTAIN; // Not a handler method, abstain
            }

            HandlerMethod handlerMethod = (HandlerMethod) handlerObject;

            RequiredScopes requiresScope = handlerMethod.getMethodAnnotation(RequiredScopes.class);

            if (requiresScope == null) {
                // Try to get the annotation from the bean class (controller class) if it's not on the method
                Class<?> beanType = handlerMethod.getBeanType();
                if (beanType != null) {
                    requiresScope = beanType.getAnnotation(RequiredScopes.class);
                }
                if(requiresScope == null){
                    return ACCESS_ABSTAIN;
                }

            }

            String[] requiredScopes = requiresScope.value();
            List<String> requiredScopesList = Arrays.asList(requiredScopes);

            if (authentication == null || !authentication.isAuthenticated()) {
                return ACCESS_DENIED; // Not authenticated, deny access
            }

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            boolean hasRequiredScope = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> {
                        String authorityWithoutPrefix = authority.startsWith("SCOPE_") ? authority.substring(6) : authority;
                        return requiredScopesList.contains(authorityWithoutPrefix);
                    });

            if (hasRequiredScope) {
                return ACCESS_GRANTED;
            } else {
                return ACCESS_DENIED;
            }

        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return ACCESS_ABSTAIN; // Abstain on error
        }
    }
}