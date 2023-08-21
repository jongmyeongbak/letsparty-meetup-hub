package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.PartyTag;

@Mapper
public interface PartyTagMapper {
	
	// 파티 태그 추가
	void insertTags(List<PartyTag> partyTags);
	
	// 파티 번호로 파티 태그 모두 조회
	List<PartyTag> getTagsByPartyNo(int partyNo);

	void deleteTagByPartyNo(int partyNo);

}
