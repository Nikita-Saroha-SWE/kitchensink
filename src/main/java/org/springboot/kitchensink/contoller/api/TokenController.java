package org.springboot.kitchensink.contoller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class TokenController {

    private final OAuth2AuthorizedClientService clientService;

    public TokenController(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/token")
    public ResponseEntity<String> getIdToken(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not an OAuth2 user");
        }

        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        String principalName = oauthToken.getName();

        OAuth2AuthorizedClient authorizedClient =
                clientService.loadAuthorizedClient(registrationId, principalName);

        if (authorizedClient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authorized client");
        }

        OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();

        // This is the ID Token (a JWT)
        String idToken = oidcUser.getIdToken().getTokenValue();

        return ResponseEntity.ok(idToken);
    }
}
