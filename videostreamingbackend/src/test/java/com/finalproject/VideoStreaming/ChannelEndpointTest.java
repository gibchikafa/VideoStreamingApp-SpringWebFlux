package com.finalproject.VideoStreaming;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.finalproject.VideoStreaming.application.UserChannelsService;
import com.finalproject.VideoStreaming.application.UserService;
import com.finalproject.VideoStreaming.configuration.AppEndpointConfiguration;
import com.finalproject.VideoStreaming.domain.Role;
import com.finalproject.VideoStreaming.domain.User;
import com.finalproject.VideoStreaming.domain.UserStreamingChannel;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@WebFluxTest
@Import({AppEndpointConfiguration.class, UserService.class, UserRequestsHandler.class, 
	UserChannelHandler.class, UserChannelsService.class, AuthenticatedUser.class, 
	CORSFilter.class, SecurityConfig.class, SecurityContextRepository.class, 
	JwtUtil.class, AuthenticationManager.class, String.class, Role.class})

public class ChannelEndpointTest {
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final MediaType jsonUtf8 = MediaType.APPLICATION_JSON_UTF8;
	//String jwt;
	
	@MockBean
	private UserRepository repository;
	
//	@MockBean
//	private UserChannelsService service;
	
	private final WebTestClient client;
	
	public ChannelEndpointTest(@Autowired WebTestClient client) {
		this.client = client;
	}
	
//	@Before
//	public void initialize() {
//		User user = createDefaultUser();
//		
//		Mono<User> initMono = Mono.just(user);
//		Mockito
//		.when(this.repository.save(user))
//		.thenReturn(initMono);
//		
//		
//	}
	
	@Test
	public void getAllChannelsTest() {
		List<User> usersList = new ArrayList<>();
		List<UserStreamingChannel> allChannelsList = new ArrayList<>();
		
		for(int i = 0; i < 5; i++) {
			User newUser = createDefaultUser();
			UserStreamingChannel channel = createDefaultChannel();
			channel.setUserId(newUser.getId());
			
			List<UserStreamingChannel> channelList = new ArrayList<>();
			channelList.add(channel);
			allChannelsList.add(channel);
			
			newUser.setLiveChannels(channelList);
			
			usersList.add(newUser);
		}
		
		Flux<User> withChannels = Flux.fromIterable(usersList);
		
		Mockito
			.when(this.repository.allLiveChannels())
			.thenReturn(withChannels);
		
		this.
			client
			.get()
			.uri("/channels")
			.accept(jsonUtf8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(jsonUtf8)
            .expectBody()
            .jsonPath("$..liveChannels").isNotEmpty();
	}
	
	@Test
	public void userChannelsTest() {
		User demoUser = createDefaultUser();
		UserStreamingChannel channel = createDefaultChannel();
		channel.setUserId(demoUser.getId());
		
		List<UserStreamingChannel> channelList = new ArrayList<>();
		channelList.add(channel);
		demoUser.setLiveChannels(channelList);
		
		Mockito
			.when(this.repository.findById(demoUser.getId()))
			.thenReturn(Mono.just(demoUser));
	
		
		this.
			client
			.get()
			.uri("/user-channels/" + demoUser.getId())
			.accept(jsonUtf8)
	        .exchange()
	        .expectStatus()
	        .isUnauthorized();
	        
//	        isOk()
//	        .expectHeader().contentType(jsonUtf8)
//	        .expectBody()
//	        .jsonPath("$..liveChannels[0].id").isEqualTo(channel.getId());
		
	}
	
	@Test
	public void getChannelInfoTest() {
		User demoUser = createDefaultUser();
		UserStreamingChannel channel1 = createDefaultChannel();
		channel1.setUserId(demoUser.getId());
		
		UserStreamingChannel channel2 = createDefaultChannel();
		channel2.setUserId(demoUser.getId());
		
		List<UserStreamingChannel> channelList = new ArrayList<>();
		channelList.add(channel1);
		channelList.add(channel2);
		demoUser.setLiveChannels(channelList);
		
		Mockito
			.when(this.repository.findById(demoUser.getId()))
			.thenReturn(Mono.just(demoUser));
		
		this.
			client
			.get()
			.uri("/channel-info/" + demoUser.getId() + "/" + channel2.getId())
			.accept(jsonUtf8)
	        .exchange()
	        .expectStatus().isOk()
	        .expectHeader().contentType(jsonUtf8)
	        .expectBody()
	        .jsonPath("$..liveChannels[0].id").isEqualTo(channel2.getId());
	}
	
	@Test
	public void addChannelTest() {
		User demoUser = createDefaultUser();
		UserStreamingChannel channel = createDefaultChannel();
		channel.setUserId(demoUser.getId());
		
		List<UserStreamingChannel> channelList = new ArrayList<>();
		channelList.add(channel);
		demoUser.setLiveChannels(channelList);
		
		Mockito
			.when(this.repository.findById(demoUser.getId()))
			.thenReturn(Mono.just(demoUser));
		
		Mockito
			.when(this.repository.save(demoUser))
			.thenReturn(Mono.just(demoUser));
		
		this
	        .client
	        .post()
	        .uri("/channels")
	        .contentType(jsonUtf8)
	        .body(Mono.just(channel), UserStreamingChannel.class)
	        .exchange()
	        .expectStatus().isCreated()
	        .expectHeader().contentType(jsonUtf8);
	}
	
	//create default channel
	private UserStreamingChannel createDefaultChannel() {
		String channelId = UUID.randomUUID().toString();
		UserStreamingChannel channel = new UserStreamingChannel();
		channel.setId(channelId);
		channel.setDescription("Test Channel");
		channel.setStartTime(dateFormat.format(new Date()));

		return channel;
	}
	
	//Create default user
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
