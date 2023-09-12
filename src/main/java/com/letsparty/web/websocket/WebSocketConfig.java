package com.letsparty.web.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.letsparty.web.websocket.interceptor.ChatChannelInterceptor;
import com.letsparty.web.websocket.interceptor.HttpSessionHandshakeInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final ChatChannelInterceptor chatChannelInterceptor;
	private final ChatWebSocketHandlerDecoratorFactory chatWebSocketHandlerDecoratorFactory;
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				// HttpSession을 WS로 변경할 때 오직 roomId를 전달하기 위한 Interceptor
				.addInterceptors(new HttpSessionHandshakeInterceptor())
//				// apic 테스트 및 외부 url 접속을 위함
				.setAllowedOriginPatterns("*")
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

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(chatChannelInterceptor);
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		// WebSocketSession을 강제로 닫을 수 있도록 직접 접근하는 수단
		registry.addDecoratorFactory(chatWebSocketHandlerDecoratorFactory);
	}
}
