package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.Category;

@Mapper
public interface CategoryMapper {
	
	// 카테고리 조회
	Category getCategoryByNo(int no);
	
	// 카테고리 모두 조회
	List<Category> getAllCategories();
	
}
