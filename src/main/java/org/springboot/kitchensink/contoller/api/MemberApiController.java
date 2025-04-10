package org.springboot.kitchensink.contoller.api;

import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberApiController {

	@Autowired
	private UserService userService;

	@GetMapping("/me")
	public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
		if (jwt == null || jwt.getClaimAsString("sub") == null) {
			return ResponseEntity.status(401).body("Unauthorized: Missing OAuth2 user");
		}

		String userId = jwt.getClaimAsString("sub");
		User user = userService.findByUserId(userId);

		if (user == null) {
			return ResponseEntity.status(404).body("User not found");
		}

		Member member = user.getMember();
		if (member == null) {
			return ResponseEntity.status(404).body("Member profile not found");
		}

		return ResponseEntity.ok(member);
	}

}
