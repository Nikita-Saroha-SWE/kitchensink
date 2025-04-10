package org.springboot.kitchensink.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springboot.kitchensink.collections.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
    	OidcUser oAuth2User = super.loadUser(userRequest);

        // Process user and persist to MongoDB
        User user = userService.loadUser(oAuth2User);

        // Wrap with Spring Security's user object
        
        Set<SimpleGrantedAuthority> authorities = new HashSet<>(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getMember().getRole())));

		return new DefaultOidcUser(authorities, userRequest.getIdToken(), "sub");
		
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
