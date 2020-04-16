package com.finalproject.VideoStreaming.application;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.finalproject.VideoStreaming.domain.User;
import com.finalproject.VideoStreaming.domain.UserStreamingChannel;
import com.finalproject.VideoStreaming.repository.UserRepository;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/*
 * Service for the channels
 */
@Log4j2
@Service
public class UserChannelsService {
    private final UserRepository userRepository;
    
    public UserChannelsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> userChannels(String id) {
        return this.userRepository.findById(id);
    }
    
	public Mono<User> addLiveChannel(UserStreamingChannel channel){
		String id = channel.getUserId();
		String channelId = channel.getId();
		return this.userRepository.findById(id).map(thisUser -> {
			List<UserStreamingChannel> channels = thisUser.getLiveChannels();
			channels.add(channel);
			
			thisUser.setLiveChannels(channels);
			
			return thisUser;
		}).flatMap(newUser -> {
			return this.userRepository.save(newUser);
		}).flatMap(user -> {
			return this.getChannelInfo(id, channelId);
		});
	}
	
	public Mono<User> getChannelInfo(String userId, String channelId){
		return this.userRepository.findById(userId).map(user -> {
			List<UserStreamingChannel> channels = user.getLiveChannels();
			
			List<UserStreamingChannel> queryChannel = channels.stream().filter(channel -> {
				return channel.getId().equalsIgnoreCase(channelId);
			}).collect(Collectors.toList());
			
			user.setLiveChannels(queryChannel);
			
			return user;
		});
	}
	
	public Mono<User> removeChannel(String userId, String channelId){
		return this.userRepository.findById(userId).map(thisUser -> {
			List<UserStreamingChannel> channels = thisUser.getLiveChannels();
			channels.clear();
			thisUser.setLiveChannels(channels);
			log.info(thisUser);
			return thisUser;
		}).flatMap(newUser -> {
			return userRepository.save(newUser);
		});
	}
	
	public Mono<Boolean> channelExist(String userId, String channelId) {
		return this.userRepository.findById(userId).map(user -> {
			List<UserStreamingChannel> channels = user.getLiveChannels();
			
			List<UserStreamingChannel> queryChannel = channels.stream().filter(channel -> {
				return channel.getId().equalsIgnoreCase(channelId);
			}).collect(Collectors.toList());
			
			if(queryChannel.size() > 0) {
				return true;
			}
			else {
				return false;
			}
		});
	}
	
	public Flux<User> getAllLiveChannels(){
		return this.userRepository.allLiveChannels();
	}
	
}
