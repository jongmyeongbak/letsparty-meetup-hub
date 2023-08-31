package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.letsparty.vo.ChatRoom;

@Mapper
public interface ChatRoomMapper {

	boolean insertChatRoom(ChatRoom chatRoom);
	
	ChatRoom findById(String id);
	List<ChatRoom> findAllByPartyNoAndCreatorNo(@Param("partyNo") int partyNo, @Param("creatorNo") int creatorNo);
	List<ChatRoom> findAllByPartyNoAndCreatorNoAndIsPublicAndChattersCnt(@Param("partyNo") int partyNo, @Param("creatorNo") int creatorNo, @Param("isPublic") boolean isPublic, @Param("chattersCnt") int chattersCnt);
	String findOneToOneRoomIdByPartyNo(@Param("partyNo") int partyNo, @Param("userNo1") int userNo1, @Param("userNo2") int userNo2);
	List<ChatRoom> findAllByPartyNoAndUserNo(@Param("partyNo") int partyNo, @Param("userNo") int userNo);
	
	void updateChatRoom(ChatRoom chatRoom);
	void increaseChattersCntById(String id);
	void decreaseChattersCntById(String id);
	
	void deleteChatRoomById(String roomId);
}
