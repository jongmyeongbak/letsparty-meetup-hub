package com.letsparty.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.CategoryMapper;
import com.letsparty.mapper.PartyMapper;
import com.letsparty.mapper.PartyReqMapper;
import com.letsparty.mapper.PartyTagMapper;
import com.letsparty.mapper.UserMapper;
import com.letsparty.util.PartyDataUtils;
import com.letsparty.vo.Category;
import com.letsparty.vo.Party;
import com.letsparty.vo.PartyReq;
import com.letsparty.vo.PartyTag;
import com.letsparty.vo.User;
import com.letsparty.web.form.PartyCreateForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartyService {
	
	private final UserMapper userMapper;
	private final PartyMapper partyMapper;
	private final CategoryMapper categoryMapper;
	private final PartyReqMapper partyReqMapper;
	private final PartyTagMapper partyTagMapper;
	
	public int createParty(PartyCreateForm partyCreateForm, String leaderId) {	
		
		Party party = new Party();
		BeanUtils.copyProperties(partyCreateForm, party);
		party.setName(party.getName().trim()); 
		
		party.setNo(1);
		
		// 파티 리더
		User leader = userMapper.getUserById(leaderId);
		party.setLeader(leader);
		
		// 카테고리 
		Category category = categoryMapper.getCategoryByNo(partyCreateForm.getCategoryNo());
		party.setCategory(category);
		
		String filename = (partyCreateForm.getSavedName() != null) ? partyCreateForm.getSavedName() : partyCreateForm.getDefaultImagePath();
		party.setFilename(filename);
		log.info("저장하는 이미지의 이름 ====> {}", partyCreateForm.getSavedName());
		
//		// 파티 테이블에 파티 추가
//		partyMapper.createParty(party);
//		
//		// 파티 - 게시물 시퀀스 추가
//		partyMapper.createPartySequence(party.getNo());
//		
//		// 가입 조건테이블에 조건 추가
//		PartyReq partyReq = new PartyReq();
//		// 최소나이 조건 추가
//		partyReq.setParty(party);
//		partyReq.setName("생년1");
//		partyReq.setValue(partyCreateForm.getBirthStart());
//		partyReq.setDescription("가입할 수 있는 최대생년 (예: 0000년 이후 출생자)");
//		partyReqMapper.insertPartyReq(partyReq);
//		
//		// 최대나이 조건 추가
//		partyReq.setParty(party);
//		partyReq.setName("생년2");
//		partyReq.setValue(partyCreateForm.getBirthEnd());
//		partyReq.setDescription("가입할 수 있는 최대생년 (예: 0000년 이전 출생자)");
//		partyReqMapper.insertPartyReq(partyReq);
//		
//		// 성별 조건 추가
//		partyReq.setParty(party);
//		partyReq.setName("성별");
//		partyReq.setValue(partyCreateForm.getGender());
//		partyReq.setDescription("가입할 수 있는 성별");
//		partyReqMapper.insertPartyReq(partyReq);
		
		// 생성된 파티 넘버
		int partyNo = party.getNo();
		
		// 1. 파티 태그 추가 - 클라이언트에서 구분해서 받아옴
		List<String> tagsFromForm = partyCreateForm.getTags();
		List<PartyTag> existingTags = partyTagMapper.getTagsByPartyNo(partyNo);
		log.info("태그들 ====> {}", existingTags);
		
		Set<String> existingTagNames = new HashSet<>();
		if (existingTags != null) {
			for (PartyTag existingTag : existingTags) {
				existingTagNames.add(existingTag.getName());
			}
		}
		
		if (tagsFromForm != null) {
			for (String tag : tagsFromForm) {
				if (!existingTagNames.contains(tag)) {
					PartyTag newTag = new PartyTag();
					newTag.setParty(party); 		// 파티 번호
					newTag.setCategory(category);	// 카테고리 번호
					newTag.setName(tag);
					log.info("태그이름 ====> {}",newTag.getName());
					partyTagMapper.insertTag(newTag);
				}
			}
		}
				
		return partyNo;
	}
	
	
	// 파티 번호로 파티 조회
	public Party getPartyByNo(int partyNo) {
		return partyMapper.getPartyByNo(partyNo);
	}
	
}
