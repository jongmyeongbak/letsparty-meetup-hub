package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.PartyReq;

@Mapper
public interface PartyReqMapper {
	
	// 파티 가입 조건 추가
	void insertPartyReqs(List<PartyReq> partyReqs);
}
