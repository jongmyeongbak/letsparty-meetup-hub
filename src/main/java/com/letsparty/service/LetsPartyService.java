package com.letsparty.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.letsparty.info.Pagination;
import com.letsparty.mapper.LetsPartyMapper;
import com.letsparty.vo.Category;
import com.letsparty.vo.LetsPartyPost;
import com.letsparty.vo.Party;
import com.letsparty.vo.User;
import com.letsparty.web.form.LetsPartyPostForm;
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
		int end = pagination.getEnd();
		
		param.put("begin", begin);
		param.put("end", end);
		
		LetsPartyPostList result = new LetsPartyPostList();
		List<LetsPartyPost> posts = letsPartyMapper.getPosts(param);
		
		if (posts == null) {
		    posts = new ArrayList<>();
		}
		
		result.setPagination(pagination);
		result.setPosts(posts);
		
		return result;
	}
}
