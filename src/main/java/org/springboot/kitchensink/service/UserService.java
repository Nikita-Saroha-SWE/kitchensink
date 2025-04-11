package org.springboot.kitchensink.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService{
	
	@Autowired
	private MemberService memberService;

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User loadUser(OidcUser oidcUser) {
//		Map<String, Object> attributes = oidcUser.getAttributes();
		String userId = oidcUser.getName();
		String username = oidcUser.getGivenName()+" "+oidcUser.getFamilyName();
		String email = oidcUser.getEmail();

		Optional<User> existingUser = userRepository.findByUserId(userId);
		
		User user = null;
		if (existingUser.isPresent()) {
			user = existingUser.get();
		} else {
			Member member = null;
//			user = userRepository.save(user);
			Member existingMember = memberService.findByUserId(userId);
			if(existingMember != null && existingMember.getRole().equalsIgnoreCase("ADMIN")) {
				member = existingMember;
			}else {
				user = new User();
				user.setUserId(userId);
				user.setUsername(username);
				member = memberService.registerMemberWithUser(email, user.getUserId(), "USER");
			}
			user.setMember(member);
			user = userRepository.save(user);
		}
		
		return user;
	}
	
	public User findByUserId(String userId) {
		return userRepository.findByUserId(userId).orElse(null);
	}
	
	public User saveUser(User user) {
		return userRepository.save(user);
	}
	
	public List<User> listUsers(){
		return userRepository.findAllByOrderByUsernameAsc();
	}
	
	public void promoteToAdmin(String id) {
        userRepository.findById(id).ifPresent(user -> {
            user.getMember().setRole("ADMIN");
            memberService.register(user.getMember());
            userRepository.save(user);
        });
    }
	
	public void demoteAdmin(String id) {
        userRepository.findById(id).ifPresent(user -> {
            user.getMember().setRole("USER");
            memberService.register(user.getMember());
            userRepository.save(user);
        });
    }
	
	public void deleteById(String id) {
		userRepository.findById(id).ifPresent(user -> {
	        if (user.getMember() != null && user.getMember().getId() != null) {
	        	memberService.deleteById(user.getMember().getId());
	        }
	        userRepository.deleteById(id);
	    });
	}
	
}
