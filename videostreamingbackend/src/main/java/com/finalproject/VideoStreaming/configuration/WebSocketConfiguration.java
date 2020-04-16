package com.finalproject.VideoStreaming.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.finalproject.VideoStreaming.application.UserChannelsService;

import lombok.extern.log4j.Log4j2;

/*
 * Web socket configuration
 * We specify the routes and its handler
 * Streaming Client connects at route /streamer/{uid}/{cid} where uid is user id and cid is channel id
 * Channel Subscriber connects at route /client/{id}, id is the channel id
 */
@Log4j2
@Configuration
public class WebSocketConfiguration {
    @Bean
    HandlerMapping handlerMapping(UserChannelsService channelsService) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/streamer/{uid}/{cid}", new StreamingClientWebsocketHandler(channelsService));
        map.put("/client/{id}", new StreamReceiverWebsocketHandler());
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setUrlMap(map);

        simpleUrlHandlerMapping.setOrder(10);
        return simpleUrlHandlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
