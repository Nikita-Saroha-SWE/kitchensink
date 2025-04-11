package org.springboot.kitchensink.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.contoller.api.AdminApiController;
import org.springboot.kitchensink.service.MemberService;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminApiController.class)
class AdminApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private MemberService memberService;

    private User testUser;
    private Member testMember;

    @BeforeEach
    void setup() {
        testMember = new Member();
        testMember.setId("m1");
        testMember.setUserId("u1");
        testMember.setName("First Member");
        testMember.setPhoneNumber("9876543210");
        testMember.setEmail("test@example.com");

        testUser = new User();
        testUser.setId("u1");
        testUser.setUserId("u1");
        testUser.setMember(testMember);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        when(userService.listUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/api/admin/members").with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].member.email").value("test@example.com"));
    }

    @Test
    void updateMember_shouldReturnUpdatedMember() throws Exception {
        when(memberService.updateMember(any(Member.class))).thenReturn(testMember);
        when(userService.findByUserId("u1")).thenReturn(testUser);
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/admin/member")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("First Member"));
    }

    @Test
    void promote_shouldSucceed() throws Exception {
        doNothing().when(userService).promoteToAdmin("u1");

        mockMvc.perform(post("/api/admin/promote/u1").with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk());

        verify(userService).promoteToAdmin("u1");
    }

    @Test
    void demote_shouldSucceed() throws Exception {
        doNothing().when(userService).demoteAdmin("u1");

        mockMvc.perform(post("/api/admin/demote/u1").with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk());

        verify(userService).demoteAdmin("u1");
    }

    @Test
    void delete_shouldSucceed() throws Exception {
        doNothing().when(userService).deleteById("u1");

        mockMvc.perform(delete("/api/admin/delete/u1").with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk());

        verify(userService).deleteById("u1");
    }
}
