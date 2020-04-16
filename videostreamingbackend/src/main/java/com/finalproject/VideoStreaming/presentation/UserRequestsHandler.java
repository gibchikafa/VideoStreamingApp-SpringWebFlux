package com.finalproject.VideoStreaming.presentation;

import java.net.URI;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.finalproject.VideoStreaming.application.UserService;
import com.finalproject.VideoStreaming.domain.User;
import com.finalproject.VideoStreaming.domain.UserAllDetailsDTO;
import com.finalproject.VideoStreaming.domain.UserDTO;
import com.finalproject.VideoStreaming.domain.UserStreamingChannel;
import com.finalproject.VideoStreaming.utils.JwtUtil;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/*
 * Controller handling all user related requests: create user, login, get user info
 */
@Log4j2
@Component
public class UserRequestsHandler {
    private final UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil tokenProvider;
    
    public UserRequestsHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> getById(ServerRequest r) {
        return defaultReadResponse(this.userService.getUserInfo(r.pathVariable("id")));
    }
    
    public Mono<ServerResponse> updateInfo(ServerRequest r){
    	Mono<User> mono = r
                .bodyToMono(User.class)
                .flatMap(toWrite -> this.userService.updateUserInfo(toWrite));
    	return defaultReadResponse(mono);
    }
    
    public Mono<ServerResponse> createUser(ServerRequest r){
    	return r
                .bodyToMono(User.class)
                .map(user -> {
                	user.setPassword(passwordEncoder.encode(user.getPassword()));
                	return user;
                })
                .flatMap(user -> this.userService.getUserByUsername(user.getUsername())
                		.flatMap(dbUser -> 
                			ServerResponse
                				.badRequest()
                				.contentType(MediaType.APPLICATION_JSON_UTF8)
                				.body(BodyInserters.fromObject(new ApiErrorResponse(400, "User already exist")))
                		)
                		.switchIfEmpty(
	                		this.userService.createUser(user)
	                			.flatMap(created -> 
	                				ServerResponse
	                					.created(URI.create("/user/" + created.getId()))
	                					.contentType(MediaType.APPLICATION_JSON_UTF8).build())
                		)
                );
    }

    public Mono<ServerResponse> updatePassword(ServerRequest r){
    	return ServerResponse
               .ok()
               .contentType(MediaType.TEXT_PLAIN)
               .body("work-in-progress", String.class);
    }
    
    public Mono<ServerResponse> login(ServerRequest r){
    		return r
    			.bodyToMono(User.class)
    			.flatMap(loginUser -> this.userService.getUserByUsername(loginUser.getUsername())
    						.flatMap(user -> {
    							if(passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
    								return 
    			    						ServerResponse
    			    							.ok()
    			    							.contentType(MediaType.APPLICATION_JSON_UTF8)
    			    							.body(BodyInserters.fromValue(new LoginResponse(tokenProvider.generateToken(user), user.getFullname(), user.getUsername(), user.getId())));
    							}
    							else {
    								return 
    										ServerResponse.badRequest().body(BodyInserters.fromObject(new ApiErrorResponse(400, "Invalid password")));
    							}
			    				
    						})
    						.switchIfEmpty(
    								ServerResponse.badRequest().body(BodyInserters.fromObject(new ApiErrorResponse(400, "Username does not exist")))
    						)
    			);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<User> users) {
        return Mono
                .from(users)
                .flatMap(p -> {
                	return ServerResponse
                    .created(URI.create("/user/" + p.getId()))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .build();
                });
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<User> users) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(users, User.class);
    }
    
    private class LoginResponse{
    	private String jwt;
    	private String id;
    	private String fullname;
    	private String username;
    	
    	public LoginResponse(String jwt, String fullname, String username, String id) {
    		this.jwt = jwt;
    		this.fullname = fullname;
    		this.username = username;
    		this.id = id;
    	}
    	
    	public String getJwt() {
    		return this.jwt;
    	}
    	
    	public String getFullname() {
    		return this.fullname;
    	}
    	
    	public String getUsername() {
    		return this.username;
    	}
    	
    	public String getId() {
    		return this.id;
    	}
    }
    
    private class ApiErrorResponse{
    	private int errorCode;
    	private String message;
    	
    	public ApiErrorResponse(int errorcode, String message) {
    		this.errorCode = errorCode;
    		this.message = message;
    	}
    	
    	public int getErrorCode() {
    		return this.errorCode;
    	}
    	
    	public void setErrorCode(int code) {
    		this.errorCode = code;
    	}
    	
    	public String getMessage() {
    		return this.message;
    	}
    	
    	public void setMessage(String message) {
    		this.message = message;
    	}
    }
}
