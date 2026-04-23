package vn.hieu.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hieu.jobhunter.domain.Permission;
import vn.hieu.jobhunter.domain.Role;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.service.UserService;
import vn.hieu.jobhunter.util.SecurityUtil;
import vn.hieu.jobhunter.util.error.PermissionException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println("Processing preHandle");
        System.out.println("Path: " + path);
        System.out.println("Method: " + httpMethod);
        System.out.println("URI: " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUserByUsername(email);
            if (user != null) {
                // 1. Check if token was issued before password change
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth) {
                    java.time.Instant iat = jwtAuth.getToken().getIssuedAt();
                    if (iat != null && user.getPasswordLastChangedAt() != null) {
                        if (iat.isBefore(user.getPasswordLastChangedAt().truncatedTo(java.time.temporal.ChronoUnit.SECONDS))) {
                            throw new PermissionException("Token has been invalidated because the password was changed.");
                        }
                    }
                }

                // 2. Check path authorization
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream().anyMatch(item -> item.getApiPath().equals(path)
                            && item.getMethod().equals(httpMethod));

                    if (isAllow == false) {
                        throw new PermissionException("You do not have permission to access this endpoint.");
                    }
                } else {
                    throw new PermissionException("No role assigned. Access denied.");
                }
            }
        }

        return true;
    }
}
