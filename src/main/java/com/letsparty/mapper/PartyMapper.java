package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.Party;

@Mapper
public interface PartyMapper {
	
	// 새파티 생성
	void createParty(Party party);
	
	// 파티 생성과 동시에 파티 게시물 시퀀스 생성
	void createPartySequence(int partyNo);
	
	// 파티 조회
	Party getPartyByNo(int partyNo);
	
	// 파티 수정
	void updateParty(Party party);
	
}
