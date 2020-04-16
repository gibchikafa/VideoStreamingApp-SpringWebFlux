package com.finalproject.VideoStreaming.domain;

import java.util.List;

public interface UserAllDetailsDTO {
	String getId();
	String getUsername();
	String getFullname();
	List<UserStreamingChannel> getLiveChannels();
}
