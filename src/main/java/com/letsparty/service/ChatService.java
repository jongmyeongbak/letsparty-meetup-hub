package com.letsparty.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.letsparty.dto.ChatRoomWithUsers;
import com.letsparty.dto.ChatUserResponse;
import com.letsparty.mapper.ChatMessageMapper;
import com.letsparty.mapper.ChatRoomMapper;
import com.letsparty.mapper.ChatUserMapper;
import com.letsparty.mapper.UserPartyApplicationMapper;
import com.letsparty.security.user.LoginUser;
import com.letsparty.vo.ChatMessage;
import com.letsparty.vo.ChatRoom;
import com.letsparty.vo.ChatUser;
import com.letsparty.vo.Party;
import com.letsparty.vo.UserPartyApplication;
import com.letsparty.web.websocket.service.SessionInfoMapper;
import com.letsparty.web.websocket.service.SessionStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

	private final SimpMessagingTemplate messagingTemplate;
	private final SessionInfoMapper sessionInfoMapper;
	private final SessionStore sessionStore;
	private final ChatRoomMapper chatRoomMapper;
	private final ChatUserMapper chatUserMapper;
	private final ChatMessageMapper chatMessageMapper;
	private final UserPartyApplicationMapper userPartyApplicationMapper;
	
	public String createRoom(int partyNo, ChatRoom chatRoom) {
		Party party = new Party();
		party.setNo(partyNo);
		chatRoom.setParty(party);
		
		String roomId;
		do {
			roomId = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 6);
			chatRoom.setId(roomId);
		} while (!chatRoomMapper.insertChatRoom(chatRoom));
		return roomId;
	}
	
	public String createInitRoomOfParty(int partyNo) {
		ChatRoom chatRoom = ChatRoom.builder().isPublic(true).isEssential(true).description("파티 전체 멤버와 함께하는 채팅방").build();
		return createRoom(partyNo, chatRoom);
	}
	
	public String createOpenRoom(LoginUser loginUser, int partyNo, String title, String description) {
		ChatRoom chatRoom = ChatRoom.builder().creatorNo(loginUser.getNo()).isPublic(true).title(title).description(description).build();
		String roomId = createRoom(partyNo, chatRoom);

		joinRoom(loginUser.getNo(), roomId, chatRoom);
		
		return roomId;
	}
	
	public String createPrivateRoom(LoginUser loginUser, int partyNo, List<Integer> inviteeNos) {
		// one-to-one 채팅인 경우 이미 개설된 방을 확인
		if (inviteeNos.size() == 1) {
			String roomId = chatRoomMapper.findOneToOneRoomIdByPartyNo(partyNo, loginUser.getNo(), inviteeNos.get(0));
			// 방이 존재하면 바로 반환
			if (roomId != null) {
				return roomId;
			}
		}
		
		// 파티원 목록 조회
		List<UserPartyApplication> userPartyApplications = userPartyApplicationMapper.findAllWithUserNoByPartyNoAndStatus(partyNo, "승인", null);
		// 파티원번호 목록 추출
		Set<Integer> userNos = new HashSet<>();
		for (UserPartyApplication userPartyApplication : userPartyApplications) {
			userNos.add(userPartyApplication.getUser().getNo());
		}
		// 참여할 유저들이 파티원인지 확인
		if (!userNos.contains(loginUser.getNo()) || !userNos.containsAll(inviteeNos)) {
			throw new IllegalArgumentException("채팅에 참여 가능한 유저가 아님");
		}
		
		// 방 생성
		ChatRoom chatRoom = ChatRoom.builder().creatorNo(loginUser.getNo()).build();
		String roomId = createRoom(partyNo, chatRoom);
		
		// 방 참가
		joinRoom(loginUser.getNo(), roomId, chatRoom);
		for (Integer inviteeNo : inviteeNos) {
			joinRoom(inviteeNo, roomId, chatRoom);
		}
		
		return roomId;
	}
	
	private boolean isUserInRoom(int userNo, String roomId) {
		ChatUser chatUser = ChatUser.builder().roomId(roomId).userNo(userNo).build();
		Long lastReadMessageNo = chatUserMapper.findLastReadMessageNoByRoomNoAndUserNo(chatUser);
		return lastReadMessageNo != null;
	}
	
	public boolean joinRoomIfPossible(String userId, int userNo, String roomId) {
		// 참가상태면 true 반환
		if (isUserInRoom(userNo, roomId)) {
			return true;
		}
		// 미참가상태면 방 정보 조회
		ChatRoom chatRoom = chatRoomMapper.findById(roomId);
		//	비공개방이면 false 반환
		if (!chatRoom.isPublic()) {
			return false;
		}
		//	공개방이면 유저가 방이 속한 파티의 파티원인지 확인
		UserPartyApplication userPartyApplication = userPartyApplicationMapper.findByPartyNoAndUserId(chatRoom.getParty().getNo(), userId);
		//		파티원이 아니면: false 반환
		if (userPartyApplication == null || !userPartyApplication.getStatus().equals("승인")) {
			return false;
		}
		//		파티원이면: 참가 후 true 반환
		joinRoom(userNo, roomId, chatRoom);
		return true;
	}

	private void joinRoom(int userNo, String roomId, ChatRoom chatRoom) {
		ChatMessage chatMessage = ChatMessage.builder().roomNo(chatRoom.getNo()).type(1).userNo(userNo).createdAt(LocalDateTime.now()).build();
		chatMessageMapper.insertChatMessage(chatMessage);
		log.info("joinRoom: {}", chatMessage);
		chatUserMapper.insertChatUser(ChatUser.builder().roomId(roomId).userNo(userNo)
				.joinMessageNo(chatMessage.getNo()).lastReadMessageNo(chatMessage.getNo()).build());
		chatRoomMapper.increaseChattersCntById(roomId);
		messagingTemplate.convertAndSend("/topic/chat/" + roomId, chatMessage);
	}
	
	public void exitRoom(String userId, int userNo, String roomId) {
		ChatUser chatUser = chatUserMapper.findById(roomId, userNo);
		// 참가자가 아니라면 종료
		if (chatUser == null) {
			return;
		}
		
		// 	채팅방에 접속해있다면 웹소켓 닫기
		Set<String> sessionIds = sessionInfoMapper.getSessionIdsOfUserInRoom(userNo, roomId);
		for (String sessionId : sessionIds) {
			sessionStore.closeSession(sessionId);
		}
		
		ChatRoom chatRoom = ChatRoom.builder().id(roomId).build();
		// 채팅유저 삭제
		chatUserMapper.deleteChatUser(roomId, userNo);
		// 참가자수 감소
		chatRoomMapper.decreaseChattersCntById(chatRoom);
		ChatMessage chatMessage = ChatMessage.builder().roomNo(chatRoom.getNo()).type(2).userNo(userNo).createdAt(LocalDateTime.now()).build();
		
		// 퇴장메시지 저장
		chatMessageMapper.insertChatMessage(chatMessage);
		// 채팅방에 접속하고 있지 않다면 읽지 않은 메시지의 안읽은수 감소시키기
		if (sessionIds.isEmpty()) {
			chatMessageMapper.decreaseUnreadCntByRoomNoAndLastReadMessageNo(chatRoom.getNo(), chatUser.getLastReadMessageNo());
			// 접속 중인 유저 화면에서도 안읽은수를 감소시키기 위해 메시지번호 반영
			chatMessage.setUnreadCnt(chatUser.getLastReadMessageNo());
		}
		// 퇴장메시지 송신
		messagingTemplate.convertAndSend("/topic/chat/" + roomId, chatMessage);
		
		// 방 인원이 0일 때 필수방이 아니라면 방 삭제
		if (chatRoom.getChattersCnt() == 0 && !chatRoom.isEssential()) {
			chatRoomMapper.deleteChatRoomByNo(chatRoom.getNo());
		}
	}
	
	public List<ChatRoomWithUsers> getChatRoomByPartyNoAndUserId(int partyNo, int userNo) {
		List<ChatRoomWithUsers> dtos = new ArrayList<>();
		List<ChatRoom> chatRoomList = chatRoomMapper.findAllByPartyNoAndUserNo(partyNo, userNo);
		for (ChatRoom cr : chatRoomList) {
			ChatRoomWithUsers dto = new ChatRoomWithUsers();
			BeanUtils.copyProperties(cr, dto);
			
			List<ChatUserResponse> users = chatUserMapper.findByRoomIdWithoutMe(cr.getId(), userNo);
			dto.setChatUsers(users);
			
			dtos.add(dto);
		}
		
		return dtos;
	}
}
