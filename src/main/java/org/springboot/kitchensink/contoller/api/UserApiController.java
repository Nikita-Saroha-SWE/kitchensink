package org.springboot.kitchensink.contoller.api;

import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.service.MemberService;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
public class UserApiController {

	@Autowired
	private UserService userService;

	@Autowired
	private MemberService memberService;

	@GetMapping("/member")
	public ResponseEntity<Member> getMember(@AuthenticationPrincipal Jwt jwt) {
		String userId = jwt.getSubject();
		User user = userService.findByUserId(userId);
		return ResponseEntity.ok(user.getMember());
	}

	@PutMapping("/member")
	public ResponseEntity<?> updateMember(@Valid @RequestBody Member member) {
		Member updated = memberService.updateMember(member);
		User user = userService.findByUserId(member.getUserId());
		user.setMember(updated);
		userService.saveUser(user);
		return ResponseEntity.ok(updated);
	}
}
