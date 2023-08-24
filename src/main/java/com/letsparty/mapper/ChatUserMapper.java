package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.dto.ChatUserResponse;
import com.letsparty.vo.ChatUser;

@Mapper
public interface ChatUserMapper {

	void createChatUser(ChatUser chatUser);
	Long findLastReadMessageNoByRoomNoAndUserNo(ChatUser chatUser);
	List<ChatUserResponse> findByRoomId(int roomId);
}
