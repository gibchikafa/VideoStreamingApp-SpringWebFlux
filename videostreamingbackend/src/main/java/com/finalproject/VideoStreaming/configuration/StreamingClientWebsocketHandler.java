package com.finalproject.VideoStreaming.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.finalproject.VideoStreaming.application.StreamVideoSegmentPublisher;
import com.finalproject.VideoStreaming.application.UserChannelsService;
import com.finalproject.VideoStreaming.domain.StreamVideoSegment;
import com.finalproject.VideoStreaming.domain.StreamingClient;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.nio.ByteBuffer;
/*
 * Handler for the client streaming the video.
 * We create an emitter Flux using the StreamVideoSegmentPublisher and keep it in the StreamingClient.streamerFluxes map. A subscriber to this channel will subscribe to this Flux
 */
@Log4j2
@Component("ReactiveWebSocketHandler")
public class StreamingClientWebsocketHandler implements WebSocketHandler {
	private final UserChannelsService channelsService;
	public StreamingClientWebsocketHandler(UserChannelsService channelsService) {
		this.channelsService = channelsService;
	}
	
    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {

    	String[] urlParts = webSocketSession.getHandshakeInfo().getUri().getPath().split("/");
    	
    	String channelId = "";
    	String userId = "";
    	
    	String emptyString = "";
    	if(urlParts.length == 4) {
    		userId = urlParts[2];
    		channelId = urlParts[3];
    		
    		log.info(userId + " " + channelId);
    	}
    	
    	if(!channelId.equals(emptyString) && !userId.equals(emptyString)) {
    		StreamVideoSegmentPublisher publisher = new StreamVideoSegmentPublisher(Executors.newSingleThreadExecutor());
        	Flux<StreamVideoSegment> publish = Flux.create(publisher).publish().autoConnect();
        	
        	StreamingClient.streamerFluxes.put(channelId, publish);
        	
        	return webSocketSession.receive().doOnNext(message -> {
        		
        		StreamVideoSegment segment = extractSegment(message, webSocketSession.getHandshakeInfo().getUri().getPath().split("/")[3]);
        		publisher.receiveSegment(segment);
        	}).doFinally(signal -> {
        		//code to do after the user disconnects
        		//remove channel in db
        		//remove the publisher
        		String uid = webSocketSession.getHandshakeInfo().getUri().getPath().split("/")[2];
        		String cid = webSocketSession.getHandshakeInfo().getUri().getPath().split("/")[3];
        		
        		log.info("Closing connection " + uid
        				+ " " + cid
        				);
        		
        		StreamingClient.streamerFluxes.remove(cid);
        		channelsService.removeChannel(uid, cid).subscribe();
        		
        	}).then();
    	}
    	else {
    		return webSocketSession.close();
    	}
    	
    }

    private StreamVideoSegment extractSegment(WebSocketMessage message, String sessionId) {
        ByteBuffer buffer = message.getPayload().asByteBuffer();
        byte[] b = new byte[buffer.remaining()];
        buffer.get(b);
        StreamVideoSegment segment = new StreamVideoSegment();
        segment.setStreamId(sessionId);
        segment.setVideoSegmentBytes(b);
        
        return segment;
    }
}
