package org.springboot.kitchensink.config;

import java.io.IOException;

import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
    	
        
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String userId = oauthUser.getAttribute("sub").toString();
        
        if (userId == null) {
            response.sendRedirect("/login?error=missing-id");
            return;
        }
        User user = userService.findByUserId(userId);

        if (user != null && user.getMember() != null) {
            String role = user.getMember().getRole();
            
            switch (role.toUpperCase()) {
	            case "ADMIN":
	                response.sendRedirect("/admin/home");
	                break;
	            case "USER":
	                response.sendRedirect("/user/home");
	                break;
	            default:
	                response.sendRedirect("/");
	                break;
	        }
        } else {
            response.sendRedirect("/login?error=user-not-found");
        }
    }
}
