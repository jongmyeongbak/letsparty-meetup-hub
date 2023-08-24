package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.ChatRoom;

@Mapper
public interface ChatRoomMapper {

	boolean createChatRoom(ChatRoom chatRoom);
	ChatRoom findById(String roomId);
	void updateChatRoom(ChatRoom chatRoom);
//	void increaseChattersCntBy~();
//	void decreaseChattersCntBy~();
//	void deleteChatRoomBy~();
}
