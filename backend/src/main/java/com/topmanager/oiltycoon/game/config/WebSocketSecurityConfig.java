package com.topmanager.oiltycoon.game.config;

import com.topmanager.oiltycoon.social.model.UserRole;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
                .simpDestMatchers("/app/**").authenticated()
                .simpMessageDestMatchers("/topic/**").denyAll()
                .simpSubscribeDestMatchers("/user/**", "/topic/**").authenticated()
                .anyMessage().denyAll();

    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
