package org.springboot.kitchensink.controller.ui;

import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@Autowired
    private UserService userService;
	
	@GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
    	if (oidcUser == null) {
            return "redirect:/login";
        }
        String userId = oidcUser.getSubject();
        User user = userService.findByUserId(userId);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        if(user.getMember() != null && user.getMember().getRole().equalsIgnoreCase("admin")) {
        	return "admin/adminhome";
        }
        return "user/register";
    }
	
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
