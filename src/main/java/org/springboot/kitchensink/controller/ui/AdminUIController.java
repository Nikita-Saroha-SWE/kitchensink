package org.springboot.kitchensink.controller.ui;

import org.springboot.kitchensink.collections.User;
import org.springboot.kitchensink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUIController {

    @Autowired
    private UserService userService;

    private String getCurrentUserId(OidcUser oidcUser) {
        return oidcUser != null ? oidcUser.getSubject() : null;
    }

    @GetMapping("/home")
    public String adminHome(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        String userId = getCurrentUserId(oidcUser);
        if (userId == null) return "redirect:/login";

        User user = userService.findByUserId(userId);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "admin/adminhome"; // just shows layout; data fetched via JS
    }

    @GetMapping("/members")
    public String dashboard(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        String userId = getCurrentUserId(oidcUser);
        if (userId == null) return "redirect:/login";

        User user = userService.findByUserId(userId);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "admin/dashboard"; // just layout, JS fetches list
    }
}
