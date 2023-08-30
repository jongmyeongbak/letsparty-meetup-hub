package com.letsparty.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.CategoryMapper;
import com.letsparty.mapper.LetsPartyMapper;
import com.letsparty.mapper.PartyMapper;
import com.letsparty.mapper.UserMapper;
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
	private final PartyMapper partyMapper;
	private final CategoryMapper categoryMapper;
	private final UserMapper userMapper;
	
	// 렛츠파티 게시물 추가
	public void insertPost(LetsPartyPostForm letsPartyPostForm, String userid) {
		LetsPartyPost letsPartyPost = new LetsPartyPost();
		BeanUtils.copyProperties(letsPartyPostForm, letsPartyPost);
		letsPartyPost.setTitle(letsPartyPost.getTitle().trim());
		letsPartyPost.setContent(letsPartyPost.getContent().trim());
		
		int partyNo = letsPartyPostForm.getPartyNo();
		
		Category category = categoryMapper.getCategoryByPartyNo(partyNo);
		Party party = partyMapper.getPartyByNo(partyNo);
		User user = userMapper.getUserById(userid);
		
		letsPartyPost.setParty(party);
		letsPartyPost.setCategory(category);
		letsPartyPost.setUser(user);
		
		letsPartyMapper.insertPost(letsPartyPost);
	}
	
}
