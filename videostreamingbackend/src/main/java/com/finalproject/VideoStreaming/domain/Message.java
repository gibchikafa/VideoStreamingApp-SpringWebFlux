package com.finalproject.VideoStreaming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
	String messageType;
	String textPayload;
	byte[] videoDataPayload;
}
