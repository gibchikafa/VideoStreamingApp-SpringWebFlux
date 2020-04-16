package com.finalproject.VideoStreaming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamVideoSegment {
    @Id
    private long segmentId;

    private String streamId;

    @Transient
    private byte[] videoSegmentBytes;
}
