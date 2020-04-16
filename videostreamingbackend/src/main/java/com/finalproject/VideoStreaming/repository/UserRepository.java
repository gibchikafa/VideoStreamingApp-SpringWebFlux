package com.finalproject.VideoStreaming.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import com.finalproject.VideoStreaming.domain.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
	@Query(value="{liveChannels: {$exists: true, $ne: []}}")
	Flux<User> allLiveChannels();
	
	Mono<User> findByUsername(String username);
	
	Mono<User> findByUsernameAndPassword(String username, String password);
	
//	@Query(value = "id: ?0", fields = "{'fullname' : 1, username: 1}")
//	Mono<User> getUserDetails(String id);
//	
//	@Query(value = "id: ?0", fields = "{'fullname' : 1, liveChannels: 1}")
//	Mono<User> getUserChannels(String id);
}
