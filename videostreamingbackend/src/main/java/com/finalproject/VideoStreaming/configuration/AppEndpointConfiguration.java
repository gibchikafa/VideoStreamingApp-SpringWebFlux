package com.finalproject.VideoStreaming.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.finalproject.VideoStreaming.presentation.UserChannelHandler;
import com.finalproject.VideoStreaming.presentation.UserRequestsHandler;
import com.finalproject.VideoStreaming.utils.CaseInsensitiveRequestPredicate;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/*
 * Configuration to routes to our REST APIs
 */
@Configuration
public class AppEndpointConfiguration {
    @Bean
    RouterFunction<ServerResponse> routes(UserChannelHandler channelsHandler,  UserRequestsHandler userHandler) {
        return route(i(GET("/channels")), channelsHandler::allChannels)
                .andRoute(i(GET("/channel-info/{user-id}/{id}")), channelsHandler::getChannelInfo)
                .andRoute(i(GET("/user-channels/{id}")), channelsHandler::allUserChannels)
                .andRoute(i(POST("/channels")), channelsHandler::create)
      
        		//Handlers for user specific functions
		        .andRoute(i(POST("/user/updateinfo")), userHandler::updateInfo)
		        .andRoute(i(POST("/user/changePassword")), userHandler::updatePassword)
		        .andRoute(i(GET("/user/{id}")), userHandler::getById)
        		.andRoute(i(POST("/create")), userHandler::createUser)
        		.andRoute(i(POST("/login")), userHandler::login);
    }

    private static RequestPredicate i(RequestPredicate target) {
        return new CaseInsensitiveRequestPredicate(target);
    }
}
