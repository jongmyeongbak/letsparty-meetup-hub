package com.letsparty.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.PartyReq;

@Mapper
public interface PartyReqMapper {
	
	// 파티 가입 조건 추가
	void insertPartyReqs(List<PartyReq> partyReqs);
	
	// 파티 가입 조건 조회
	List<PartyReq> getPartyReqsByNo(int partyNo);

	void updatePartyReqs(Map<String, Object> reqs);
}
