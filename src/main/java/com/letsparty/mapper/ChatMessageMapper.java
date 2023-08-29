package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.ChatMessage;

@Mapper
public interface ChatMessageMapper {

	void insertChatMessage(ChatMessage chatMessage);
}
