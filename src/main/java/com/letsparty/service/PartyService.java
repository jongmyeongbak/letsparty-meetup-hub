package com.letsparty.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.CategoryMapper;
import com.letsparty.mapper.PartyMapper;
import com.letsparty.mapper.PartyReqMapper;
import com.letsparty.mapper.PartyTagMapper;
import com.letsparty.mapper.UserMapper;
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
		
		// 파티 리더
		User leader = userMapper.getUserById(leaderId);
		party.setLeader(leader);
		
		// 카테고리 
		Category category = categoryMapper.getCategoryByNo(partyCreateForm.getCategoryNo());
		party.setCategory(category);
		
		// 파일 이름 저장
		String filename = (partyCreateForm.getSavedName() != null) ? partyCreateForm.getSavedName() : partyCreateForm.getDefaultImagePath();
		party.setFilename(filename);
		log.info("저장하는 이미지의 이름 ====> {}", partyCreateForm.getSavedName());
		
		// 파티 테이블에 파티 추가
		partyMapper.createParty(party);
		
		// 파티 - 게시물 시퀀스 추가
		partyMapper.createPartySequence(party.getNo());
		
		// 가입 조건테이블에 조건 추가
		String birthStart = partyCreateForm.getBirthStart();
		String birthEnd = partyCreateForm.getBirthEnd();
		String gender = partyCreateForm.getGender();
		
		List<PartyReq> partyReqs = new ArrayList<>();
		
		// 최소나이 조건 추가
		partyReqs.add(createPartyReq(party, "생년1", birthStart));
		// 최대나이 조건 추가
		partyReqs.add(createPartyReq(party, "생년2", birthEnd));
		// 성별 조건 추가
		partyReqs.add(createPartyReq(party, "성별", gender));

		partyReqMapper.insertPartyReqs(partyReqs);
		
		// 생성된 파티 넘버
		int partyNo = party.getNo();
		
		// 1. 파티 태그 추가 - 클라이언트에서 구분해서 받아옴
		List<String> tagsFromForm = partyCreateForm.getTags();
		log.info("태그들 ====> {}", tagsFromForm);
		
		if (tagsFromForm != null && !tagsFromForm.isEmpty()) {
			List<PartyTag> newTags = new ArrayList<>();
			for (String tag : tagsFromForm) {
				PartyTag newTag = new PartyTag();
				newTag.setParty(party); 		// 파티 번호
				newTag.setCategory(category);	// 카테고리 번호
				newTag.setName(tag);
				newTags.add(newTag);
			}
			partyTagMapper.insertTags(newTags);
		}
				
		return partyNo;
	}
	
	// PartyReq 객체를 생성해주는 메서드
	private PartyReq createPartyReq(Party party, String name, String value) {
	    PartyReq partyReq = new PartyReq();
	    partyReq.setParty(party);
	    partyReq.setName(name);
	    partyReq.setValue(value);
	    return partyReq;
	}
	
	// 파티 번호로 파티 조회
	public Party getPartyByNo(int partyNo) {
		return partyMapper.getPartyByNo(partyNo);
	}
	
}
