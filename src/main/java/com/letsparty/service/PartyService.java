package com.letsparty.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.letsparty.web.form.PartyForm;

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
	
	public int createParty(PartyForm partyCreateForm, String leaderId) {	
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
		
		// 파티 태그 추가 - 클라이언트에서 구분해서 받아옴
		if (partyCreateForm.getTags() != null && !partyCreateForm.getTags().isEmpty()  ) {
			List<String> tagsFromForm = partyCreateForm.getTags();
			insertTags(tagsFromForm, party);
		}
				
		return partyNo;
	}
	
	public void modifyParty(PartyForm partyModifyForm, int partyNo) {
		// 기존 파티 정보 조회
	    Party party = partyMapper.getPartyByNo(partyNo);
	    
	    // 파티 기존 파티 정보 복사
	    BeanUtils.copyProperties(partyModifyForm, party);
	    party.setName(party.getName().trim());
	    
	    // 파일 이름 수정
	    String filename = (partyModifyForm.getSavedName() != null) ? partyModifyForm.getSavedName() : partyModifyForm.getDefaultImagePath();
	    party.setFilename(filename);
	    
	    // 파티 테이블에 변경사항 저장
	    partyMapper.updateParty(party);
	    
	    // 가입 조건 수정
	    Map<String, Object> updatePartyReqs = new HashMap<>();
	    String birthStart = partyModifyForm.getBirthStart();
	    String birthEnd = partyModifyForm.getBirthEnd();
	    String gender = partyModifyForm.getGender();
	    
	    updatePartyReqs.put("partyNo", party.getNo());
	    updatePartyReqs.put("birthStart", birthStart);
	    updatePartyReqs.put("birthEnd", birthEnd);
	    updatePartyReqs.put("gender", gender);
	    
	    partyReqMapper.updatePartyReqs(updatePartyReqs);
	    
	    // 기존의 모두 태그 삭제
	    partyTagMapper.deleteTagByPartyNo(partyNo);
	    
	    // 수정폼에서 입력한 태그 저장
 		List<String> tagsFromForm = partyModifyForm.getTags();
 		
 		if (tagsFromForm != null && !tagsFromForm.isEmpty()) {
 			insertTags(tagsFromForm, party);
 		}
	}
	
	// Tag를 추가해주는 메서드
	public void insertTags(List<String> tagsFromForm, Party party) {
		List<PartyTag> newTags = new ArrayList<>();
		for (String tag : tagsFromForm) {
			PartyTag newTag = new PartyTag();
			newTag.setParty(party);
			newTag.setCategory(party.getCategory());
			newTag.setName(tag);
			newTags.add(newTag);
		}
		partyTagMapper.insertTags(newTags);
	}
	
	// PartyReq 객체를 생성해주는 메서드
	public PartyReq createPartyReq(Party party, String name, String value) {
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
	
	// 파티 번호로 파티 조건 검색
	public List<PartyReq> getPartyReqsByNo(int partyNo){
		return partyReqMapper.getPartyReqsByNo(partyNo);
	}
}
