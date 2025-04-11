package org.springboot.kitchensink.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.contoller.api.UserApiController;
import org.springboot.kitchensink.service.MemberService;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserApiController.class)
class UserApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private MemberService memberService;

    private String userId = "google-user-123";
    private Member member;
    private User user;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor mockJwt() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", userId)
                .build();
        return jwt().jwt(jwt);
    }

    @BeforeEach
    void setup() {
        member = new Member("Alice", "alice@example.com", "1234567890", "USER", userId);
        member.setId("m1");

        user = new User("alice", userId);
        user.setMember(member);
    }

    @Test
    void getMember_shouldReturnMemberOfCurrentUser() throws Exception {
        when(userService.findByUserId(userId)).thenReturn(user);

        mockMvc.perform(get("/api/user/member")
                        .with(mockJwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));
    }

    @Test
    void updateMember_shouldUpdateAndReturnUpdatedMember() throws Exception {
        when(memberService.updateMember(any(Member.class))).thenReturn(member);
        when(userService.findByUserId(userId)).thenReturn(user);
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/user/member")
                        .with(mockJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));
    }
}
