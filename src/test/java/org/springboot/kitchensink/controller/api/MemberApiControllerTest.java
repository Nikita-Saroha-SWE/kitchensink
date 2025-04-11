package org.springboot.kitchensink.controller.api;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.contoller.api.MemberApiController;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberApiController.class)
class MemberApiControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private UserService userService;

    private final String userId = "google-user-123";

    private JwtRequestPostProcessor jwtWithSub(String userId) {
        Jwt jwt = Jwt.withTokenValue("fake-token")
                .header("alg", "none")
                .claim("sub", userId)
                .build();
        return jwt().jwt(jwt);
    }

    @Test
    void shouldReturn401IfJwtMissing() throws Exception {
        mockMvc.perform(get("/api/members/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404IfUserNotFound() throws Exception {
        when(userService.findByUserId(userId)).thenReturn(null);

        mockMvc.perform(get("/api/members/me").with(jwtWithSub(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void shouldReturn404IfMemberNotFound() throws Exception {
        User user = new User("testuser", userId);
        user.setMember(null);

        when(userService.findByUserId(userId)).thenReturn(user);

        mockMvc.perform(get("/api/members/me").with(jwtWithSub(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Member profile not found"));
    }

    @Test
    void shouldReturnMemberProfileIfAllValid() throws Exception {
        Member member = new Member("Alice", "alice@example.com", "1234567890", "ADMIN", userId);
        User user = new User("testuser", userId);
        user.setMember(member);

        when(userService.findByUserId(userId)).thenReturn(user);

        mockMvc.perform(get("/api/members/me")
                        .with(jwtWithSub(userId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));
    }
}
