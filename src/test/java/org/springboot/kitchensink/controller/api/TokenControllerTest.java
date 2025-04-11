package org.springboot.kitchensink.controller.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springboot.kitchensink.contoller.api.TokenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TokenController.class)
class TokenControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private OAuth2AuthorizedClientService clientService;

    private static final String MOCK_ID_TOKEN = "mock-id-token";

    private OidcUser mockOidcUser() {
        OidcIdToken idToken = new OidcIdToken(
                MOCK_ID_TOKEN,
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("sub", "google-user-id", "email", "test@example.com")
        );
        return new DefaultOidcUser(List.of(), idToken);
    }

    @Test
    void shouldReturnIdToken_whenValidOAuth2User() throws Exception {
        OidcUser oidcUser = mockOidcUser();
        OAuth2AuthenticationToken auth = new OAuth2AuthenticationToken(
                oidcUser,
                oidcUser.getAuthorities(),
                "google"
        );

        OAuth2AuthorizedClient client = mock(OAuth2AuthorizedClient.class);
        when(clientService.loadAuthorizedClient("google", auth.getName())).thenReturn(client);

        mockMvc.perform(get("/auth/token")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(content().string(MOCK_ID_TOKEN));
    }

    @Test
    void shouldReturn401_whenNotOAuth2User() throws Exception {
        mockMvc.perform(get("/auth/token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Not an OAuth2 user"));
    }

    @Test
    void shouldReturn401_whenAuthorizedClientIsNull() throws Exception {
        OidcUser oidcUser = mockOidcUser();
        OAuth2AuthenticationToken auth = new OAuth2AuthenticationToken(
                oidcUser,
                oidcUser.getAuthorities(),
                "google"
        );

        when(clientService.loadAuthorizedClient("google", auth.getName())).thenReturn(null);

        mockMvc.perform(get("/auth/token")
                        .with(authentication(auth)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("No authorized client"));
    }
}
