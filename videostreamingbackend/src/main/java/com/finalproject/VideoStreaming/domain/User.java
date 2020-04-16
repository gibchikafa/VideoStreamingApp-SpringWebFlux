package com.finalproject.VideoStreaming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
/*
 * Our domain object.
 * We are using MongoDB so we keep live channels and roles in the same collection. No relational mapping.
 */
public class User implements UserDTO, UserAllDetailsDTO{
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;
    
    private String fullname;
    
    private String password;
    
    private List<Role> roles;
    
    private List<UserStreamingChannel> liveChannels = new ArrayList<>();
}
