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
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserUIController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String userHome(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        if (oidcUser == null) return "redirect:/login";

        String userId = oidcUser.getSubject();
        User user = userService.findByUserId(userId);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "user/register"; // Data fetched via JS
    }
}
