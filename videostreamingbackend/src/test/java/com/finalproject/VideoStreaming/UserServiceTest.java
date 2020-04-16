package com.finalproject.VideoStreaming;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import com.finalproject.VideoStreaming.application.UserService;
import com.finalproject.VideoStreaming.domain.User;
import com.finalproject.VideoStreaming.repository.UserRepository;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Log4j2
@DataMongoTest
@Import(UserService.class)
public class UserServiceTest {
	private final UserService userService;
	private final UserRepository userRepository;

	
	public UserServiceTest(@Autowired UserService service, 
            @Autowired UserRepository repository) {
		this.userService = service;
		this.userRepository = repository;
	}
	
	@Test
	public void createUserTest() {
		User demoUser = new User();
		demoUser.setFullname("John Smith Test");
		demoUser.setUsername("johnsmith");
		demoUser.setPassword("123456");
		
		Mono<User> created = this.userService.createUser(demoUser);
		
		StepVerifier
			.create(created)
			.expectNextMatches(user -> StringUtils.hasText(user.getId()))
			.verifyComplete();
	}
	
	@Test
	public void getUserInfoTest() {
		String testId = UUID.randomUUID().toString();
		User demoUser = new User();
		demoUser.setId(testId);
		demoUser.setFullname("John Smith Test");
		demoUser.setUsername(RandomStringUtils.randomAlphabetic(10));
		demoUser.setPassword("123456");
		
		Mono<User> created = this.userService.createUser(demoUser);
		
		StepVerifier
			.create(created)
			.expectNextMatches(user -> StringUtils.hasText(user.getId()) && testId.equalsIgnoreCase(user.getId()))
			.verifyComplete();
		
		
	}
	
	@Test
	public void updateUserInfoTest() {
		String username = RandomStringUtils.randomAlphabetic(10);;
		User demoUser = new User();
		demoUser.setFullname("John Doe");
		demoUser.setUsername(username);
		demoUser.setPassword("123456");
		
		String newFullname = RandomStringUtils.randomAlphanumeric(10);
		String newUsername = RandomStringUtils.randomAlphanumeric(10);
		
		Mono<User> updated = this.userService.createUser(demoUser).flatMap(user -> {
			user.setFullname(newFullname);
			user.setUsername(newUsername);
			
			return this.userService.updateUserInfo(user);
		});
		
		StepVerifier
			.create(updated)
			.expectNextMatches(user -> newFullname.equals(user.getFullname()) 
					&& newUsername.equals(user.getUsername()))
			.verifyComplete();
	}
	
	@Test
	public void wrongOldPasswordUpdateTest() {
		String testId = UUID.randomUUID().toString();
		String username = RandomStringUtils.randomAlphabetic(10);
		String msg1 = "password-no-match";
		String msg2 = "success";
		
		User demoUser = new User();
		demoUser.setId(testId);
		demoUser.setFullname("John Doe");
		demoUser.setUsername(username);
		demoUser.setPassword("123456");
		
		//make old password wrong
		String oldPassword = "1234";
		String newPassword = RandomStringUtils.randomAlphabetic(10);
		Mono<String> resultString1 = this.userService.createUser(demoUser).flatMap(user -> {
			return this.userService.updatePassword(user.getId(), oldPassword, newPassword);
		});
		
		StepVerifier
			.create(resultString1)
			.expectNextMatches(message -> message.equalsIgnoreCase(msg1))
			.verifyComplete();	
	}
	
	@Test
	public void rightOldPasswordTest() {
		String testId = UUID.randomUUID().toString();
		String msg2 = "success";
		
		User demoUser = new User();
		demoUser.setId(testId);
		demoUser.setFullname("John Doe");
		demoUser.setUsername(RandomStringUtils.randomAlphabetic(10));
		demoUser.setPassword("123456");
		
		//make old password right
		String oldPassword2 = "123456";
		String newPassword2 = RandomStringUtils.randomAlphabetic(10);
		Mono<String> resultString2 = this.userService.createUser(demoUser).flatMap(user -> {
			return this.userService.updatePassword(user.getId(), oldPassword2, newPassword2);
		});
		
		StepVerifier
		.create(resultString2)
		.expectNextMatches(message -> message.equals(msg2))
		.verifyComplete();
	}
}
