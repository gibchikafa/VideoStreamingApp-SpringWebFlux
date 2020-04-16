package com.finalproject.VideoStreaming.application;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.finalproject.VideoStreaming.domain.User;
import com.finalproject.VideoStreaming.repository.UserRepository;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@Component
public class DemoDataInitializer implements ApplicationListener<ApplicationReadyEvent> {
	public final UserRepository userRepository;

	public  DemoDataInitializer(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		User demoUser = new User();
		demoUser.setFullname("John Smith");
		demoUser.setUsername("john");
		demoUser.setPassword("1234");
		
		Flux.just(demoUser)
		.flatMap(user -> {
			return userRepository.save(user);
		});
		
//		userRepository.deleteAll().thenMany(
//				Flux.just(demoUser)
//				.flatMap(user -> {
//					log.info("Starting Saving User");
//					return userRepository.save(user);
//				})
//		)
//		.thenMany(userRepository.findAll())
//		.subscribe(user -> {
//			log.info("User " + user.toString());
//		});
	}
	
}
