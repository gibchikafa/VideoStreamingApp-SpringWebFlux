package com.finalproject.VideoStreaming.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.finalproject.VideoStreaming.utils.JwtUtil;
import static com.finalproject.VideoStreaming.utils.Constants.AUTHORITIES_KEY;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/*
 * Uses the JwtUtil class to verify the token. Also checks if the token is not expired.
 */
@Log4j2
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager{
	@Autowired
	private JwtUtil tokenProvider;

	@Override
	@SuppressWarnings("unchecked")
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();
		String username;
		try {
			username = tokenProvider.getUsernameFromToken(authToken);
		} catch (Exception e) {
			username = null;
		}
		if (username != null && ! tokenProvider.isTokenExpired(authToken)) {
			Claims claims = tokenProvider.getAllClaimsFromToken(authToken);
			log.info(claims);
			List<String> roles = (List<String>) claims.get(AUTHORITIES_KEY, List.class);
		
			log.info(roles.get(0).getClass());

//			log.info("Roles is " + roles);
//			List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

			List<SimpleGrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
			
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, username, authorities);
            SecurityContextHolder.getContext().setAuthentication(new AuthenticatedUser(username, authorities));
			return Mono.just(auth);
		} else {
			return Mono.empty();
		}
	}
}
