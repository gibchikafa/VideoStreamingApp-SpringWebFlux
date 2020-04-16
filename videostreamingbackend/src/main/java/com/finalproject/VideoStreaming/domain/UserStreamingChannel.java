package com.finalproject.VideoStreaming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStreamingChannel {
    @Id
    private String Id;
    
    private String userId;
        
    private String startTime;

    private String description;
    
    private String status;
}
