package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.PartyReq;

@Mapper
public interface PartyReqMapper {
	
	// 파티 가입 조건 추가
	void insertPartyReq(PartyReq partyReq);
}
