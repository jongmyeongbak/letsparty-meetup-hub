package com.letsparty.web.websocket.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SessionInfoMapper {

	public static class SessionDetails {
		private String roomId;
		private int userNo;
		
		public SessionDetails(String roomId, int userNo) {
			this.roomId = roomId;
			this.userNo = userNo;
		}
		
		public String getRoomId() {
			return roomId;
		}
		public int getUserNo() {
			return userNo;
		}
	}
	
	// sessionId가 속한 roomId 및 userNo를 알기 위한 Map.
	//	제출된 메시지를 roomId에 매핑하고 userNo 정보를 추가하기 위하여
	// k: sessionId, v: SessionDetails[roomId, userNo]
	private final Map<String, SessionDetails> sessionInfoMap = new ConcurrentHashMap<>();
	// room에 접속된 userNo 수를 알기 위한 Map.
	//	제거를 위해 sessionId를 같이 저장.(한 유저의 다중 세션 접속을 허용하면 한 userNo의 sessionId가 여러 개일 수 있음.) 
	//	신규 메시지를 읽지 않은 인원수 = 방 인원 - 접속자 수
	// k: RoomId, v: Map<UserNo, Set<sessionId>>
	private final Map<String, Map<Integer, Set<String>>> roomToSessionUserMap = new ConcurrentHashMap<>();
	// 채팅방 접속자수 Map
	// k: roomId, v: number of distinct userNos
	private final Map<String, Integer> roomUserCnt = new ConcurrentHashMap<>();
	
	public void addSession(String sessionId, String roomId, int userNo) {
		sessionInfoMap.put(sessionId, new SessionDetails(roomId, userNo));
		Set<String> sessions = roomToSessionUserMap.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).computeIfAbsent(userNo, k -> new HashSet<>());
		
		if (sessions.isEmpty()) { // user가 방에 처음 추가될 때
			roomUserCnt.merge(roomId, 1, Integer::sum);
		}
		
		sessions.add(sessionId);
	}
	
	public SessionDetails removeSession(String sessionId) {
		SessionDetails sessionDetails = sessionInfoMap.remove(sessionId);
		if (sessionDetails != null) {
			String roomId = sessionDetails.getRoomId();
			int userNo = sessionDetails.getUserNo();
			
			Map<Integer, Set<String>> userSessions = roomToSessionUserMap.get(roomId);
//			if (userSessions != null) {
				Set<String> sessions = userSessions.get(userNo);
//				if (sessions != null) {
					sessions.remove(sessionId);
					if (sessions.isEmpty()) {
						userSessions.remove(userNo);
						roomUserCnt.computeIfPresent(roomId, (k, v) -> v > 1 ? v - 1 : null);
						if (userSessions.isEmpty()) {
							roomToSessionUserMap.remove(roomId);
						}
					}
//				}
//			}
		}
		return sessionDetails;
	}
	
	public SessionDetails getSessionDetails(String sessionId) {
		return sessionInfoMap.get(sessionId);
	}
	
//	public Set<Integer> getUsersInRoom(String roomId) {
//	    return roomToSessionUserMap.getOrDefault(roomId, Collections.emptyMap()).keySet();
//	}
	
	public int getUserCntInRoom(String roomId) {
		return roomUserCnt.getOrDefault(roomId, 0);
	}
}
