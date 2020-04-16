package com.finalproject.VideoStreaming.presentation;

import java.net.URI;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.finalproject.VideoStreaming.application.UserChannelsService;
import com.finalproject.VideoStreaming.domain.User;
import com.finalproject.VideoStreaming.domain.UserDTO;
import com.finalproject.VideoStreaming.domain.UserStreamingChannel;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/*
 * Handles all channel requests. 
 * All methods should return the server response
 */
@Log4j2
@Component
public class UserChannelHandler {
    private final UserChannelsService channelService;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    UserChannelHandler(UserChannelsService channelService) {
        this.channelService = channelService;
    }

    public Mono<ServerResponse> allChannels(ServerRequest r) {
        return defaultReadResponse(this.channelService.getAllLiveChannels());
    }

    public Mono<ServerResponse> allUserChannels(ServerRequest r){
    	return defaultReadResponse(this.channelService.userChannels(r.pathVariable("id")));
    }

    public Mono<ServerResponse> getChannelInfo(ServerRequest r) {
    	log.info("Received request");
        return defaultReadResponse(this.channelService.getChannelInfo(r.pathVariable("user-id"), r.pathVariable("id")));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<User> flux = request
                .bodyToFlux(UserStreamingChannel.class)
                .flatMap(toWrite -> {
                	String channelId = UUID.randomUUID().toString();
                	toWrite.setId(channelId);
                	toWrite.setStartTime(dateFormat.format(new Date()));
                	return this.channelService.addLiveChannel(toWrite);
                });
        return defaultWriteResponse(flux);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<User> userChannels) {
        return Mono
                .from(userChannels)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/" + p.getId() + "/" + p.getLiveChannels().get(0).getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<User> userChannels) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(userChannels, UserDTO.class);
    }
}
