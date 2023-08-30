package com.letsparty.service;

import org.springframework.stereotype.Service;

import com.letsparty.mapper.CategoryMapper;
import com.letsparty.mapper.LetsPartyMapper;
import com.letsparty.vo.Category;
import com.letsparty.vo.LetsPartyPost;
import com.letsparty.vo.Party;
import com.letsparty.vo.User;
import com.letsparty.web.form.LetsPartyPostForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LetsPartyService {

	private final LetsPartyMapper letsPartyMapper;
	private final CategoryMapper categoryMapper;
	
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
	
}
