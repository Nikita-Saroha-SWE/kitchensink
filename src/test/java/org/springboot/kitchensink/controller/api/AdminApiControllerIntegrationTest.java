package org.springboot.kitchensink.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springboot.kitchensink.repository.MemberRepository;
import org.springboot.kitchensink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@ActiveProfiles("test")
class AdminApiControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private MemberRepository memberRepository;

    private JwtRequestPostProcessor adminJwt;

    @BeforeEach
    void setup() {
        memberRepository.deleteAll();
        userRepository.deleteAll();

        adminJwt = jwt().jwt(Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claims(claims -> {
                    claims.put("sub", "admin-user");
                    claims.put("scope", "admin");
                    claims.put("roles", List.of("ROLE_ADMIN"));
                })
                .build()).authorities(() -> "ROLE_ADMIN");
    }

    @Test
    void testGetAllUsers_withAdminRole_shouldReturnUsers() throws Exception {
        Member member = new Member();
        member.setUserId("u1");
        member.setName("John");
        member.setEmail("john@example.com");
        memberRepository.save(member);

        User user = new User();
        user.setId("u1");
        user.setMember(member);

        userRepository.save(user);

        mockMvc.perform(get("/api/admin/members").with(adminJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].member.email").value("john@example.com"));
    }

    @Test
    void testUpdateMember_shouldUpdateAndLinkToUser() throws Exception {
        User user = new User();
        user.setId("u2");
        user.setUserId("user2");
        userRepository.save(user);

        Member member = new Member();
        member.setId("m2");
        member.setEmail("jane@example.com");
        member.setUserId("user2");
        member.setPhoneNumber("9876543210");
        member.setName("Jane Updated");

        mockMvc.perform(put("/api/admin/member")
                        .with(adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Updated"));

        User updatedUser = userRepository.findById("u2").orElseThrow();
        assertThat(updatedUser.getMember().getName()).isEqualTo("Jane Updated");
    }

    @Test
    void testPromoteUser_shouldNotThrowError() throws Exception {
    	Member member = new Member();
        member.setUserId("u3");
        member.setName("John");
        member.setEmail("john@example.com");
        memberRepository.save(member);
        
        User user = new User();
        user.setId("u3");
        user.setMember(member);
        userRepository.save(user);

        mockMvc.perform(post("/api/admin/promote/u3").with(adminJwt))
                .andExpect(status().isOk());
    }

    @Test
    void testDemoteUser_shouldNotThrowError() throws Exception {
    	Member member = new Member();
        member.setUserId("u3");
        member.setName("John");
        member.setEmail("john@example.com");
        memberRepository.save(member);
        
        User user = new User();
        user.setId("u4");
        user.setMember(member);
        userRepository.save(user);

        mockMvc.perform(post("/api/admin/demote/u4").with(adminJwt))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_shouldRemoveUserAndMember() throws Exception {
        Member member = new Member();
        member.setUserId("u5");
        member.setName("To Delete");
        member.setEmail("delete@example.com");
        memberRepository.save(member);

        User user = new User();
        user.setId("u5");
        user.setMember(member);
        userRepository.save(user);

        mockMvc.perform(delete("/api/admin/delete/u5").with(adminJwt))
                .andExpect(status().isOk());

        assertThat(userRepository.findById("u5")).isEmpty();
    }
}
