package com.finalproject.VideoStreaming.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject.VideoStreaming.domain.Message;
import com.finalproject.VideoStreaming.domain.MessageTypes;
import com.finalproject.VideoStreaming.domain.StreamVideoSegment;
import com.finalproject.VideoStreaming.domain.StreamingClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/*
 * Handler for the client subscribing to the channel
 * Checks if the streaming client is still active by checking for the publisher in the StreamingClient.streamerFluxes using the streamerId
 * If found subscribe. Else disconnect
 */
@Component
public class StreamReceiverWebsocketHandler implements WebSocketHandler {
	private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
    	String[] urlParts = session.getHandshakeInfo().getUri().getPath().split("/");
    	
    	String streamerId = "";
    	
    	if(urlParts.length > 0) {
    		streamerId = urlParts[urlParts.length - 1];
    	}
    	
    	Flux<WebSocketMessage> messageFlux;
    	
    	if(StreamingClient.streamerFluxes.containsKey(streamerId)) {
    		messageFlux = StreamingClient.streamerFluxes.get(streamerId).map(segment -> {
    			return session.binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(segment.getVideoSegmentBytes()));
    		});
    	}
    	else {
    		String messageToSend = "";
    		try {
    			Message message = new Message();
    			message.setMessageType(MessageTypes.STREAMER_NOT_CONNECTED);
				messageToSend = objectMapper.writeValueAsString(message);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
    		messageFlux = Flux.just(session.textMessage(messageToSend));
    	}
    	
    	session.receive().doFinally(signal -> {
    		//code to do after the user disconnects
    		System.out.println("User disconnected");
    	}).subscribe(inMsg -> {
            //Code to do after the stream subscriber send a message
        });
    	
    	return session.send(messageFlux);
    }
}
