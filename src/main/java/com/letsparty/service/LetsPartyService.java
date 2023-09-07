package com.letsparty.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.letsparty.dto.LetsPartyPostDto;
import com.letsparty.exception.PostNotFoundException;
import com.letsparty.info.Pagination;
import com.letsparty.mapper.LetsPartyMapper;
import com.letsparty.vo.Category;
import com.letsparty.vo.LetsPartyPost;
import com.letsparty.vo.Party;
import com.letsparty.vo.User;
import com.letsparty.web.form.LetsPartyPostForm;
import com.letsparty.web.form.LetsPartyPostModifyForm;
import com.letsparty.web.model.LetsPartyPostList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LetsPartyService {

	private final LetsPartyMapper letsPartyMapper;
	
	// 렛츠파티 게시물 추가
	public void insertPost(LetsPartyPostForm letsPartyPostForm, String userid) {
		Party party = new Party();
		party.setNo(letsPartyPostForm.getPartyNo());
		User user = new User();
		user.setId(userid);
		Category category = new Category();
		category.setNo(letsPartyPostForm.getCategoryNo());
		
		LetsPartyPost letsPartyPost = LetsPartyPost.builder()
									  .party(party)
									  .user(user)
									  .category(category)
									  .title(letsPartyPostForm.getTitle().trim())
									  .content(letsPartyPostForm.getContent().trim())
									  .build();
		letsPartyMapper.insertPost(letsPartyPost);
	}
	
	// 렛츠파티 게시물 가져오기
	public LetsPartyPostList getPosts(Map<String, Object> param) {
		int totalRows = letsPartyMapper.getTotalRows(param);
		
		int page = (int) param.get("page");
		int rows = (int) param.get("rows");
		Pagination pagination = new Pagination(rows, page, totalRows);
		
		int begin = pagination.getBegin();
		
		param.put("begin", begin);
		param.put("rows", rows);
		
		LetsPartyPostList result = new LetsPartyPostList();
		List<LetsPartyPost> posts = letsPartyMapper.getPosts(param);
		
		result.setPagination(pagination);
		result.setPosts(posts);
		
		return result;
	}

	// 렛츠파티 게시물 상세 가져오기
	public LetsPartyPostDto getPostDetail(long postNo) {
	    Optional<LetsPartyPostDto> savedPartyPostOpt = Optional.ofNullable(letsPartyMapper.getPostDetailByNo(postNo));
	    return savedPartyPostOpt.orElseThrow(() -> new PostNotFoundException("해당 게시물이 존재하지 않습니다."));
	}

	// 렛츠파티 게시물 조회수 올리기
	public void increaseReadCount(long postNo) {
		LetsPartyPostDto savedPartyPost = getPostDetail(postNo);
	    savedPartyPost.setReadCnt(savedPartyPost.getReadCnt() + 1);
	    letsPartyMapper.updatePost(savedPartyPost);
	}
	
	// 렛츠파티 게시물 업데이트
	public void updatePost(LetsPartyPostModifyForm letsPartyPostModifyForm) {
		LetsPartyPostDto letsPartyPost = new LetsPartyPostDto();
		BeanUtils.copyProperties(letsPartyPostModifyForm, letsPartyPost);
		letsPartyMapper.updatePost(letsPartyPost);
	}
	
	public List<LetsPartyPost> getPostsLimit5() {
		return letsPartyMapper.getPostsLimit5();
	}
	
}