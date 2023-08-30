package com.letsparty.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.CategoryMapper;
import com.letsparty.mapper.MediaMapper;
import com.letsparty.mapper.PartyMapper;
import com.letsparty.mapper.PartyReqMapper;
import com.letsparty.mapper.PartyTagMapper;
import com.letsparty.mapper.PlaceMapper;
import com.letsparty.mapper.UserMapper;
import com.letsparty.mapper.UserPartyApplicationMapper;
import com.letsparty.vo.Category;
import com.letsparty.vo.Media;
import com.letsparty.vo.Party;
import com.letsparty.vo.PartyReq;
import com.letsparty.vo.PartyTag;
import com.letsparty.vo.Place;
import com.letsparty.vo.Post;
import com.letsparty.vo.User;
import com.letsparty.vo.UserPartyApplication;
import com.letsparty.web.form.PartyForm;
import com.letsparty.web.form.PostForm;

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
	private final PlaceMapper placeMapper;
	private final MediaMapper mediaMapper;
	private final UserPartyApplicationMapper userPartyApplicationMapper;
	@Value("${s3.path.covers}")
	private String coversPath;
	
	public boolean isPartyMember(String userId, int partyNo) {
		UserPartyApplication userPartyApplication = userPartyApplicationMapper.findByPartyNoAndUserId(partyNo, userId);
		if (userPartyApplication != null && userPartyApplication.getStatus().equals("승인")) {
			return true;
		}
		return false;
	}
	
	public int createParty(PartyForm partyCreateForm, String leaderId) {	
		Party party = new Party();
		BeanUtils.copyProperties(partyCreateForm, party);
		party.setName(party.getName().trim());
	    party.setDescription(party.getDescription().trim());
		
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
	    party.setDescription(party.getDescription().trim());
	    
	    String fullPath = (partyModifyForm.getSavedName() != null) ? partyModifyForm.getSavedName() : partyModifyForm.getDefaultImagePath();
	    
	    // 유저가 편집한 사진 정보를 변경하지 않았을 때 
	    // 도메인을 제거한 후, 파일 이름만 다시 저장
    	String filename = fullPath.replace(coversPath, "");
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
	
	
	// 파티 번호로 파티 조회
	public Party getPartyByNo(int partyNo) {
		return partyMapper.getPartyByNo(partyNo);
	}
	
	// 파티 번호로 파티 조건 검색
	public List<PartyReq> getPartyReqsByNo(int partyNo){
		return partyReqMapper.getPartyReqsByNo(partyNo);
	}
	
	// 게시물 추가
	public void insertPost(PostForm postForm) {
		Post post = new Post();
		BeanUtils.copyProperties(postForm.getPost(), post);
//		게시물 저장 후 반환된 postId가 등록되어야 함
//		long postId = post.getId();
		
		post.setId(1);	// 삭제 필요
		
		// 폼에 지도 정보가 있다면 반환받은 게시물 id와 지도 정보 db 저장
		if (postForm.getPlace().getId() != null && !postForm.getPlace().getId().isBlank()) {
			insertPlace(postForm, 1);	// 실제 코드 insertPlace(postForm, postId);
		}
		// 폼에 사진 및 동영상이 있다면 반환받은 게시물 id와 미디어 정보 db 저장
		if (postForm.getImageName() != null || postForm.getVideoName() != null) {
			insertMedia(postForm.getImageName(), postForm.getVideoName(), post);
		}
	}
	
	// 게시물용 지도 정보 추가 메서드
	private void insertPlace(PostForm postForm, long postId) {
		Place place = new Place();
		BeanUtils.copyProperties(postForm.getPlace(), place);
		
		// place객체의 게시물 id 등록
		Post post = new Post();
		post.setId(postId);
		place.setPost(post);
		
		placeMapper.insertPlace(place);
	}
	
	// Tag를 추가해주는 메서드
	private void insertTags(List<String> tagsFromForm, Party party) {
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
	private PartyReq createPartyReq(Party party, String name, String value) {
		PartyReq partyReq = new PartyReq();
		partyReq.setParty(party);
		partyReq.setName(name);
		partyReq.setValue(value);
		return partyReq;
	}
	
	// 게시물용 미디어 추가 메서드
	private void insertMedia(List<String> imageList, List<String> videoList, Post post) {
		System.out.println("게시글의 번호는??");
		System.out.println(post.getId());
		List<Media> images = new ArrayList<>();
		List<Media> videos = new ArrayList<>();
		for (String imageName : imageList) {
			Media media = new Media();
			media.setContentType("image");
			media.setName(imageName);
			media.setPostId(post.getId());
			media.setPartyNo(1);;
			media.setUser(post.getUser());
			images.add(media);
		}
		mediaMapper.insertMedia(images);
		
		for (String videoName : videoList) {
			Media media = new Media();
			media.setContentType("video");
			media.setName(videoName);
			media.setPostId(post.getId());
			media.setPartyNo(1);
			media.setUser(post.getUser());
			videos.add(media);
		}
		mediaMapper.insertMedia(videos);
	}
	
	public List<UserPartyApplication> getUserPartyApplications(int partyNo, int myNo) {
		return userPartyApplicationMapper.findAllWithUserNoByPartyNoAndStatus(partyNo, "승인", myNo);
	}
	
	public boolean isDuplicateParty(String partyName, int categoryNo) {
		if (partyMapper.getPartyByNameAndCategoryNo(partyName, categoryNo) != null) {
			return true;
		}
		return false;
	}
}
