package com.letsparty.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.letsparty.mapper.CategoryMapper;
import com.letsparty.vo.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryMapper categoryMapper;
	
	public List<Category> getAllCategories() {
		return categoryMapper.getAllCategories();
	}
}
