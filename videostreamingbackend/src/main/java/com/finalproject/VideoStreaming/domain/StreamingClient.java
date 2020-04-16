package com.finalproject.VideoStreaming.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.finalproject.VideoStreaming.application.UserChannelsService;

import reactor.core.publisher.Flux;

public class StreamingClient {
	public static Map<String, Flux<StreamVideoSegment>> streamerFluxes = new HashMap<String, Flux<StreamVideoSegment>>();
	
	public static List<UserChannelsService> serviceInstance = new ArrayList<>();
}
