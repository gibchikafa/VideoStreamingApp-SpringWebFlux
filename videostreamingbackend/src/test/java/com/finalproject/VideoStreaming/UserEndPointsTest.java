package com.finalproject.VideoStreaming;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.finalproject.VideoStreaming.application.UserChannelsService;
import com.finalproject.VideoStreaming.application.UserService;
import com.finalproject.VideoStreaming.configuration.AppEndpointConfiguration;
import com.finalproject.VideoStreaming.domain.Role;
import com.finalproject.VideoStreaming.domain.User;
import com.finalproject.VideoStreaming.presentation.UserChannelHandler;
import com.finalproject.VideoStreaming.presentation.UserRequestsHandler;
import com.finalproject.VideoStreaming.repository.UserRepository;
import com.finalproject.VideoStreaming.security.AuthenticatedUser;
import com.finalproject.VideoStreaming.security.AuthenticationManager;
import com.finalproject.VideoStreaming.security.CORSFilter;
import com.finalproject.VideoStreaming.security.SecurityConfig;
import com.finalproject.VideoStreaming.security.SecurityContextRepository;
import com.finalproject.VideoStreaming.utils.JwtUtil;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@WebFluxTest
@Import({AppEndpointConfiguration.class,UserRequestsHandler.class, UserChannelHandler.class, 
	UserRequestsHandler.class, UserService.class, UserChannelsService.class,
	AuthenticatedUser.class, 
	CORSFilter.class, SecurityConfig.class, SecurityContextRepository.class, 
	JwtUtil.class, AuthenticationManager.class, String.class, Role.class})
public class UserEndPointsTest {
	private final WebTestClient client;
	private final MediaType jsonUtf8 = MediaType.APPLICATION_JSON_UTF8;
	
	@MockBean
	private UserRepository repository;
	
	public UserEndPointsTest(@Autowired WebTestClient client) {
		this.client = client;
	}
	
	@Test
	public void createUserTest() {
		User demoUser = this.createDefaultUser();
		
		Mockito
			.when(this.repository.save(Mockito.any(User.class)))
			.thenReturn(Mono.just(demoUser));

		this
	        .client
	        .post()
	        .uri("/create")
	        .contentType(jsonUtf8)
	        .body(Mono.just(demoUser), User.class)
	        .exchange()
	        .expectStatus().isCreated()
	        .expectHeader().contentType(jsonUtf8);
	}
	
	@Test
	public void getByIdTest() {
		User demoUser = this.createDefaultUser();
		
		Mockito
		.when(this.repository.findById(demoUser.getId()))
		.thenReturn(Mono.just(demoUser));
		
		this
	        .client
	        .get()
	        .uri("/user/" + demoUser.getId())
	        .accept(jsonUtf8)
            .exchange()
            .expectStatus()
            .isUnauthorized();
            
//            .expectHeader().contentType(jsonUtf8)
//            .expectBody()
//            .jsonPath("$.id").isEqualTo(demoUser.getId())
//            .jsonPath("$.username").isEqualTo(demoUser.getUsername());
	}
	
	@Test
	public void updateInfoTest() {
		User demoUser = this.createDefaultUser();
		
		Mockito
			.when(this.repository.findById(demoUser.getId()))
			.thenReturn(Mono.just(demoUser));
		
		Mockito
			.when(this.repository.save(demoUser))
			.thenReturn(Mono.just(demoUser));
		
		this
			.client
			.post()
			.uri("/user/updateinfo")
			.accept(jsonUtf8)
			.body(Mono.just(demoUser), User.class)
	        .exchange()
	        .expectStatus()
	        .isUnauthorized();
	        
	        
//	        isOk()
//	        .expectHeader().contentType(jsonUtf8)
//	        .expectBody()
//	        .jsonPath("$.id").isEqualTo(demoUser.getId())
//            .jsonPath("$.username").isEqualTo(demoUser.getUsername());
		
	}
	
	private User createDefaultUser() {
		String username = RandomStringUtils.randomAlphabetic(10);
		String userId = UUID.randomUUID().toString();
		
		User demoUser = new User();
		demoUser.setId(userId);
		demoUser.setFullname("John Doe");
		demoUser.setUsername(username);
		demoUser.setPassword("123456");
		
		
		return demoUser;
	}
}
