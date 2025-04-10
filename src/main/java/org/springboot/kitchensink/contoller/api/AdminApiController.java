package org.springboot.kitchensink.contoller.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.service.MemberService;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {
	
	private static final Logger log = LoggerFactory.getLogger(AdminApiController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    @GetMapping("/members")
    public List<User> getAllUsers() {
    	log.info("Fetching all users");
        return userService.listUsers();
    }

    @PutMapping("/member")
    public Member updateMember(@Valid @RequestBody Member member) {
        Member updated = memberService.updateMember(member);
        User user = userService.findByUserId(member.getUserId());
        user.setMember(updated);
        userService.saveUser(user);
        return updated;
    }

    @PostMapping("/promote/{id}")
    public void promote(@PathVariable String id) {
    	log.info("Promoting user with ID: {}", id);
        userService.promoteToAdmin(id);
    }

    @PostMapping("/demote/{id}")
    public void demote(@PathVariable String id) {
        userService.demoteAdmin(id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
    	log.info("Deleting user and member: {}", id);
        userService.deleteById(id);
    }
}
