package com.finalproject.VideoStreaming.application;

import java.util.Arrays;
import java.util.List;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.finalproject.VideoStreaming.domain.Role;
import com.finalproject.VideoStreaming.domain.User;
import com.finalproject.VideoStreaming.domain.UserStreamingChannel;
import com.finalproject.VideoStreaming.repository.UserRepository;
import com.finalproject.VideoStreaming.utils.JwtUtil;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/*
 * Service for the users
 */
@Log4j2
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flux<User> saveAll(Flux<User> users){
    	return this.userRepository.insert(users);
    }
    
    public Mono<User> getUserInfo(String id) {
        return this.userRepository.findById(id);
    }
    
    public Mono<User> getUserByUsername(String username){
    	return this.userRepository.findByUsername(username);
    }

	public Mono<User> createUser(User user) {
		//each user has a default role: client 
		user.setRoles(Arrays.asList(new Role("CLIENT", "client")));
		return this.userRepository.save(user);
	}

	public Mono<User> updateUserInfo(User user) { 
		String id = user.getId();
		return this.userRepository.findById(id).map(thisUser -> {
			thisUser.setFullname(user.getFullname());
			thisUser.setUsername(user.getUsername());
			
			return thisUser;
		}).flatMap(newUser -> {
			return userRepository.save(newUser);
		});
	}
	
	public Mono<String> updatePassword(String id, String oldPassword, String newPassword){
		return userRepository.findById(id)
		.flatMap(user -> {
			Mono<String> returnMono;
			log.info(oldPassword + " " + user.getPassword());
			if(oldPassword.equals(user.getPassword())) {
				user.setPassword(newPassword);
				returnMono = userRepository.save(user)
				.map(newUser -> {
					if(newUser == null ){
						return "fail";
					}
					else{
						return "success";
					}
				});
			}
			else {
				returnMono = Mono.just("password-no-match");
			}
			
			return returnMono;
		});
	}

	public Mono<User> login(User user) {
		return this.userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
	}
}
