package com.letsparty.service;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.letsparty.mapper.ChatRoomMapper;
import com.letsparty.mapper.ChatUserMapper;
import com.letsparty.security.user.LoginUser;
import com.letsparty.vo.ChatRoom;
import com.letsparty.vo.ChatUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomMapper chatRoomMapper;
	private final ChatUserMapper chatUserMapper;
	
	public String createRoom(ChatRoom chatRoom) {
		String roomId;
		do {
			roomId = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 6);
			chatRoom.setId(roomId);
		} while (!chatRoomMapper.createChatRoom(chatRoom));
		return roomId;
	}
	
	public String createInitRoomOfParty(int partyNo, String partyName) {
		String description = "파티 전체 멤버와 함께하는 채팅방";
		ChatRoom chatRoom = ChatRoom.builder().partyNo(partyNo).isPublic(true).title(partyName).description(description).build();
		return createRoom(chatRoom);
	}
	
	public String createPublicRoom(Principal principal, Authentication authentication, @AuthenticationPrincipal LoginUser loginUser, int partyNo, String title, String description) {
//		ChatRoom chatRoom = ChatRoom.builder().partyNo(partyNo).creatorNo(9999).isPublic(true).title(title).description(description).build();
//		return createRoom(chatRoom);
		return null;
	}
	
	public boolean isUserInRoom(String roomId, int userNo) {
//		Map<String, Object> params = new HashMap<>();
//		params.put("roomId", roomId);
//		params.put("userNo", userNo);
		ChatUser chatUser = ChatUser.builder().roomId(roomId).userNo(userNo).build();
		Long lastReadMessageNo = chatUserMapper.findLastReadMessageNoByRoomNoAndUserNo(chatUser);
		return lastReadMessageNo != null;
	}
}
