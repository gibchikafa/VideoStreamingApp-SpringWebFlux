package com.finalproject.VideoStreaming;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.finalproject.VideoStreaming.application.UserChannelsService;
import com.finalproject.VideoStreaming.application.UserService;
import com.finalproject.VideoStreaming.domain.User;
import com.finalproject.VideoStreaming.domain.UserStreamingChannel;
import com.finalproject.VideoStreaming.repository.UserRepository;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Component
@Log4j2
@DataMongoTest
@Import({UserChannelsService.class, UserService.class})
public class UserChannelsServiceTest {
	private final UserChannelsService channelsService;
	private final UserService userService;
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public UserChannelsServiceTest
		(@Autowired UserChannelsService channelsService, @Autowired UserService userService) {
		this.channelsService = channelsService;
		this.userService = userService;
	}
	
	@Test
	public void addChannelTest() {
		String userId = UUID.randomUUID().toString();

		Mono<User> updated = this.userService.createUser(createDefaultUser(userId)).flatMap(user -> {
			log.info("Add channel user  " + user);
			UserStreamingChannel channel = createDefaultChannel();
			channel.setUserId(user.getId());
			
			return this.channelsService.addLiveChannel(channel);
		});
		
		StepVerifier
			.create(updated)
			.expectNextMatches( user -> {
				return user.getLiveChannels().size() == 1;
			})
			.verifyComplete();
	}
	
	@Test
	public void getAllLiveChannelsTest() {
		List<User> usersList = new ArrayList<>();
		
		for(int i = 0; i < 10; i++) {
			User newUser;
			String userId = UUID.randomUUID().toString();
			String channelId = UUID.randomUUID().toString();
			if(i % 2 == 0) {
				newUser = createUserWithChannel(userId, channelId);
			}
			else {
				newUser = createDefaultUser(userId);
			}
			
			usersList.add(newUser);
		}
		
		
		Flux<User> allUsers = Flux.fromIterable(usersList);
		
		Flux<User> saved = this.userService.saveAll(allUsers).thenMany(this.channelsService.getAllLiveChannels());
		
		StepVerifier
			.create(saved)
			.thenConsumeWhile(user -> {
				return user.getLiveChannels().size() > 0;
			})
			.verifyComplete();
	}
	
	@Test
	public void getChannelInfoTest() {
		String userId = UUID.randomUUID().toString();
		String channelId = UUID.randomUUID().toString();
		
		User newUser = createUserWithChannel(userId, channelId);
		
		Mono<User> obtainChannel = this.userService.createUser(newUser).flatMap(user -> {
			return this.channelsService.getChannelInfo(userId, channelId);
		});
		
		StepVerifier
			.create(obtainChannel)
			.expectNextMatches(user -> {
				return user.getLiveChannels().get(0).getId().equals(channelId);
			})
			.verifyComplete();
	}
	
//	@Test
//	public void removeChannelTest() {
//		String userId = UUID.randomUUID().toString();
//		String channelId = UUID.randomUUID().toString();
//		
//		User newUser = createUserWithChannel(userId, channelId);
//		
//		Mono<User> removeChannel = this.userService.createUser(newUser).flatMap(user -> {
//			return this.channelsService.removeChannel(userId, channelId);
//		});
//		
//		StepVerifier
//			.create(removeChannel)
//			.expectNextMatches(user -> {
//				return user.getLiveChannels().size() == 0;
//			})
//			.verifyComplete();	
//	}
	
	//create user without the channels
	private User createDefaultUser(String userId){
		String username = RandomStringUtils.randomAlphabetic(10);;
		User demoUser = new User();
		demoUser.setId(userId);
		demoUser.setFullname("John Doe");
		demoUser.setUsername(username);
		demoUser.setPassword("123456");
		
		return demoUser;
	}
	
	//create a user and add a channel
	private User createUserWithChannel(String userId, String channelId) {
		UserStreamingChannel channel = createDefaultChannel();
		
		channel.setId(channelId);
		channel.setUserId(userId);
		
		User newUser = createDefaultUser(userId);
		List<UserStreamingChannel> defaultChannels = new ArrayList<UserStreamingChannel>();
		defaultChannels.add(channel);
		
		newUser.setLiveChannels(defaultChannels);
		
		return newUser;
	}
	
	//create default channel
	private UserStreamingChannel createDefaultChannel() {
		String channelId = UUID.randomUUID().toString();
		UserStreamingChannel channel = new UserStreamingChannel();
		channel.setDescription("Test Channel");
		channel.setStartTime(dateFormat.format(new Date()));
		channel.setId(channelId);

		return channel;
	}
}
