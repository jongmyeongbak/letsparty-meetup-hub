package com.letsparty.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.letsparty.dto.ChatRoomWithUsers;
import com.letsparty.dto.ChatUserResponse;
import com.letsparty.dto.UserChatSummaryDto;
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
import com.letsparty.web.websocket.dto.ChatMessageCon;
import com.letsparty.web.websocket.dto.ChatMessageJoin;
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
	
	public String createOpenRoom(int partyNo, LoginUser loginUser, String title, String description) {
		ChatRoom chatRoom = ChatRoom.builder().creatorNo(loginUser.getNo()).isPublic(true).title(title).description(description).build();
		String roomId = createRoom(partyNo, chatRoom);

		joinRoom(loginUser.getNo(), roomId, chatRoom.getNo());
		
		return roomId;
	}
	
	public String createPrivateRoom(int partyNo, LoginUser loginUser, List<Integer> inviteeNos) {
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
		long roomNo = chatRoom.getNo();
		joinRoom(loginUser.getNo(), roomId, roomNo);
		for (Integer inviteeNo : inviteeNos) {
			joinRoom(inviteeNo, roomId, roomNo);
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
		joinRoom(userNo, roomId, chatRoom.getNo());
		return true;
	}

	private void joinRoom(int userNo, String roomId, long roomNo) {
		ChatMessage chatMessage = ChatMessage.builder().roomNo(roomNo).type(1).userNo(userNo).createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).build();
		chatMessageMapper.insertChatMessage(chatMessage);
		log.info("joinRoom: {}", chatMessage);
		chatUserMapper.insertChatUser(ChatUser.builder().roomId(roomId).userNo(userNo)
				.joinMessageNo(chatMessage.getNo()).lastReadMessageNo(chatMessage.getNo()).build());
		chatRoomMapper.increaseChattersCntByNo(roomNo);
		
		ChatMessageJoin chatMessageJoin = new ChatMessageJoin(chatMessage.getNo(), 1, chatMessage.getCreatedAt());
		BeanUtils.copyProperties(chatUserMapper.findByRoomIdAndUserNo(roomId, userNo), chatMessageJoin);
		messagingTemplate.convertAndSend(String.format("/topic/chat/%s", roomId), chatMessageJoin);
	}
	
	public void exitRoom(String userId, int userNo, String roomId) {
		Long lastReadMessageNo = chatUserMapper.findLastReadMessageNoByRoomNoAndUserNo(ChatUser.builder().roomId(roomId).userNo(userNo).build());
		// 참가자가 아니라면 종료
		if (lastReadMessageNo == null) {
			return;
		}
		
		// 방 정보를 조회
		ChatRoom chatRoom = chatRoomMapper.findById(roomId);
		
		// 퇴장메시지 저장
		ChatMessage chatMessage = ChatMessage.builder().roomNo(chatRoom.getNo()).type(2).userNo(userNo).createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).build();
		chatMessageMapper.insertChatMessage(chatMessage);
		if (!sessionInfoMapper.isUserInRoom(userNo, roomId)) {
			// 방에 접속하고 있지 않으면 읽지 않은 메시지의 안읽은수 감소
			chatMessageMapper.decreaseUnreadCntByRoomNoAndLastReadMessageNo(chatRoom.getNo(), lastReadMessageNo);
			// 접속 중인 유저 화면에서도 안읽은수를 감소시키기 위해 메시지번호 반영
			chatMessage.setUnreadCnt(lastReadMessageNo);
		}
		
		// 퇴장메시지 송신
		//	구독자가 본인이면 창이 닫히도록
		//	구독자가 타인이면 퇴장메시지가 출력되고 읽지 않은 메시지의 안읽은수가 감소되도록
		messagingTemplate.convertAndSend(String.format("/topic/chat/%s", roomId), chatMessage);
		if (chatRoom.getChattersCnt() == 1 && !chatRoom.isEssential()) {
			// 채팅유저 삭제
			chatUserMapper.deleteChatUser(roomId, userNo);
			// 방의 마지막 인원이었을 때 필수방이 아니라면 방도 삭제
			chatRoomMapper.deleteChatRoomByNo(chatRoom.getNo());
		} else {
			// 참가자수 감소
			chatRoomMapper.decreaseChattersCntByNo(chatRoom.getNo());
			// 채팅유저 삭제
			chatUserMapper.deleteChatUser(roomId, userNo);
		}
		
		// 여전히 채팅방에 접속해있다면 웹소켓연결 강제종료
		Set<String> sessionIds = sessionInfoMapper.getSessionIdsOfUserInRoom(userNo, roomId);
		if (sessionIds != null) {
			Set<String> sessionIdsCopy = new HashSet<>(sessionIds);
			for (String sessionId : sessionIdsCopy) {
				sessionStore.closeSession(sessionId);
			}
		}
	}
	
	public List<ChatRoomWithUsers> getChatRoomByPartyNoAndUserId(int partyNo, int userNo) {
		List<ChatRoomWithUsers> dtos = new ArrayList<>();
		List<ChatRoom> chatRoomList = chatRoomMapper.getAccessibleRoomsByPartyNoAndUserNo(partyNo, userNo);
		for (ChatRoom cr : chatRoomList) {
			ChatRoomWithUsers dto = new ChatRoomWithUsers();
			BeanUtils.copyProperties(cr, dto);
			
			List<ChatUserResponse> users = chatUserMapper.findWithoutMeByRoomId(cr.getId(), userNo);
			dto.setChatUsers(users);
			
			dtos.add(dto);
		}
		
		return dtos;
	}

	public Object[] getInitInfoByRoomId(String roomId, int userNo) {
		ChatUser chatUser = ChatUser.builder().roomId(roomId).userNo(userNo).build();
		chatUser = chatUserMapper.findByRoomNoAndUserNo(chatUser);
		// 참가한 room이 아니면 연결 거부
		if (chatUser == null) {
			return null;
		}
		List<ChatUserResponse> chatUserResponses = chatUserMapper.findWithoutMeByRoomId(roomId, userNo);
		
		// 방에 접속하면 안읽은수 감소
		long lastReadMessageNo = chatUser.getLastReadMessageNo();
		if (!sessionInfoMapper.isUserInRoom(userNo, roomId)) {
			ChatMessageCon chatMessageCon = new ChatMessageCon(3, lastReadMessageNo);
			messagingTemplate.convertAndSend(String.format("/topic/chat/%s", roomId), chatMessageCon);
			chatMessageMapper.decreaseUnreadCntByRoomIdAndLastReadMessageNo(roomId, lastReadMessageNo);
		}
		List<ChatMessage> chatMessages = chatMessageMapper.getLatestMessagesForChatUser(chatUser);
		
		return new Object[]{chatUserResponses, chatMessages, lastReadMessageNo};
	}

	public ChatUserResponse getUserByRoomIdAndUserNo(String roomId, int userNo) {
		return chatUserMapper.findByRoomIdAndUserNo(roomId, userNo);
	}
	
	public List<ChatUserResponse> getUsersByRoomIdRegardlessOfLeft(String roomId, Set<Integer> userNos) {
		List<ChatUserResponse> chatUserResponses = chatUserMapper.findByRoomIdRegardlessOfLeft(roomId);
	    return chatUserResponses.stream()
	            .filter(userResponse -> userNos.contains(userResponse.getUserNo()))
	            .collect(Collectors.toList());
	}
	
	public ChatRoom getChatRoom(String roomId) {
		return chatRoomMapper.findById(roomId);
	}

	public List<UserChatSummaryDto> getUserChatSummary(int userNo) {
		return chatUserMapper.getUserChatSummary(userNo);
	}
}
