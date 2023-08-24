package com.letsparty.web.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.letsparty.web.websocket.interceptor.ChatChannelInterceptor;
import com.letsparty.web.websocket.interceptor.HttpSessionHandshakeInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
//public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer {
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final ChatChannelInterceptor chatChannelInterceptor;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				// HttpSession을 WS로 변경할 때 오직 roomId를 전달하기 위한 Interceptor
				.addInterceptors(new HttpSessionHandshakeInterceptor())
//				// apic 테스트를 위함
//				.setAllowedOriginPatterns("*");
				.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setPreservePublishOrder(true)
				// 메시지를 서버로 제출할 때
				.setApplicationDestinationPrefixes("/app")
				// 메시지를 발행할 때. 이곳으로 제출하면 바로 발행되기는 하지만, 유효성 처리 및 가공을 위해 preSend에서 제출을 막음
				.enableSimpleBroker("/topic");
	}

//	AbstractSecurityWebSocketMessageBrokerConfigurer 상속 시 사용 가능
//	@Override
//	protected boolean sameOriginDisabled() {
//		return true;
//	}
//	@Override
//	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//		messages
//			.simpDestMatchers("/app/**").authenticated()
//			.simpSubscribeDestMatchers("/topic/**").authenticated()
//			.anyMessage().denyAll();
//	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(chatChannelInterceptor);
	}

}
