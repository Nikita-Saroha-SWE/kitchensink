package org.springboot.kitchensink.config;

import org.springboot.kitchensink.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
	
	@Autowired
    private CustomOAuth2SuccessHandler successHandler;
	
	@Autowired
	private CustomOidcUserService customOidcUserService;

	@Bean
	@Order(1)
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/api/**")
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

		return http.build();
	}
	
	@Bean
	@Order(2)
    public SecurityFilterChain uiFilterChain(HttpSecurity http) throws Exception {
        http
			.authorizeHttpRequests(
					auth -> auth.requestMatchers("/login", "/css/**", "/images/**", "/webjars/**", "/oauth2/**").permitAll()
							.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
							.requestMatchers("/user/**").hasAuthority("ROLE_USER")
							.anyRequest().authenticated())
			.oauth2Login(oauth -> oauth.loginPage("/login")
					.userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService))
					.successHandler(successHandler))
			.logout(logout -> logout.logoutUrl("/logout")
					.logoutSuccessUrl("https://accounts.google.com/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:8080/")
					.invalidateHttpSession(true)
					.deleteCookies("JSESSIONID")
					.clearAuthentication(true)
					.permitAll())
			.csrf(csrf -> csrf
				    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				);
        return http.build();
    }

	@Bean
	public JwtDecoder jwtDecoder() {
		return JwtDecoders.fromIssuerLocation("https://accounts.google.com"); // adjust as needed
	}
}
