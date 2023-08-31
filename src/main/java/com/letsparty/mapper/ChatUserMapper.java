package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.letsparty.dto.ChatUserResponse;
import com.letsparty.vo.ChatUser;

@Mapper
public interface ChatUserMapper {

	boolean insertChatUser(ChatUser chatUser);
	Long findLastReadMessageNoByRoomNoAndUserNo(ChatUser chatUser);
	List<ChatUserResponse> findByRoomId(String roomId);
	List<ChatUserResponse> findByRoomIdWithoutMe(@Param("roomId") String roomId, @Param("myNo") int myNo);
}
