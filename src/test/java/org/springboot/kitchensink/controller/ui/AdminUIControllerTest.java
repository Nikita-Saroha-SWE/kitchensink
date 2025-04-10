package org.springboot.kitchensink.controller.ui;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminUIController.class)
class AdminUIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private DefaultOidcUser createOidcUser(String subject) {
        OidcIdToken idToken = new OidcIdToken(
                "id-token",
                Instant.now(),
                Instant.now().plusSeconds(60),
                new HashMap<>() {{put("sub",subject);}}
//                Map.of("sub", subject, "email", "admin@example.com")
        );

        return new DefaultOidcUser(
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                idToken,
                new OidcUserInfo(Map.of("email", "admin@example.com")),
                "sub"
        );
    }

    @Test
    void testAdminHome_WhenUserExists_ShouldReturnAdminHomeView() throws Exception {
        String userId = "google-oauth-user-123";
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("Admin Test");

        Mockito.when(userService.findByUserId(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/admin/home")
                        .with(oidcLogin().oidcUser(createOidcUser(userId))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/adminhome"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testAdminDashboard_WhenUserExists_ShouldReturnDashboardView() throws Exception {
        String userId = "google-oauth-user-123";
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("Admin Test");

        Mockito.when(userService.findByUserId(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/admin/members")
                        .with(oidcLogin().oidcUser(createOidcUser(userId))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testAdminHome_WhenUserNull_ShouldRedirectToLogin() throws Exception {
    	String userId = "google-oauth-user-123";
    	Mockito.when(userService.findByUserId(userId)).thenReturn(null);
    	
        mockMvc.perform(get("/admin/home")
                        .with(oidcLogin().oidcUser(createOidcUser(userId))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}