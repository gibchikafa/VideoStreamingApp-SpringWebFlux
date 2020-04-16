package com.finalproject.VideoStreaming.domain;

import java.util.List;

public interface UserDTO {
	String getFullname();
	String getId();
	List<UserStreamingChannel> getLiveChannels();
}
