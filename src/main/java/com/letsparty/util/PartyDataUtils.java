package com.letsparty.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;

import com.letsparty.service.CategoryService;
import com.letsparty.vo.Category;

public class PartyDataUtils {
	
	private PartyDataUtils() {};

	public static void addBirthYearAndCategoryList(Model model, CategoryService categoryService) {
	    // 가입 조건 나이 선택 목록 반복문
	    LocalDate now = LocalDate.now();
	    int year = now.getYear();
	    List<Integer> birthYears = new ArrayList<>();
	    for (int i = year - 13; i > year - 100; i--) {
	        birthYears.add(i);
	    }
	    model.addAttribute("birthYears", birthYears);
	    model.addAttribute("currentYear", year);

	    // 카테고리 목록 반복문
	    List<Category> categories = categoryService.getAllCategories();
	    model.addAttribute("categories", categories);
	}
}
