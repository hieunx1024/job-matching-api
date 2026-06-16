package vn.hieu.jobhunter.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.service.CompanyService;
import vn.hieu.jobhunter.service.RoleService;
import vn.hieu.jobhunter.service.UserService;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    private final CompanyService companyService;
    private final UserService userService;
    private final RoleService roleService;

    public PermissionInterceptorConfiguration(CompanyService companyService, UserService userService,
            RoleService roleService) {
        this.companyService = companyService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/",
                "/api/v1/auth/**",
                "/storage/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api/notifications/last24h",
                "/api/v1/payments/vnpay-return",
                "/api/v1/chat"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}
