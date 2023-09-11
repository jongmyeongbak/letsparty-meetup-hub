package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.letsparty.vo.ChatMessage;
import com.letsparty.vo.ChatUser;

@Mapper
public interface ChatMessageMapper {

	void insertChatMessage(ChatMessage chatMessage);
	
    List<ChatMessage> getLatestMessagesForChatUser(ChatUser chatUser);
	
	void decreaseUnreadCntByRoomNoAndLastReadMessageNo(@Param("roomNo") long roomNo, @Param("lastReadMessageNo") long lastReadMessageNo);
	void decreaseUnreadCntByRoomIdAndLastReadMessageNo(@Param("roomId") String roomId, @Param("lastReadMessageNo") long lastReadMessageNo);
}
