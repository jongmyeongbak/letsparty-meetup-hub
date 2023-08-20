package com.letsparty.web.form;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@Slf4j
public class PartyModifyForm {
	
	private int categoryNo;
	
	@NotBlank(message = "파티 이름은 필수 입력 값입니다.")
	@Size(max = 100, message = "파티 이름은 100자를 넘길 수 없습니다.")
	private String name;
	
	@Min(value = 10, message = "최소 정원은 10명 이상입니다.")
	private int quota;
	
	private String birthStart;
	private String birthEnd;
	private String gender;
	
	@Size(max = 255, message = "파티 설명은 255자를 넘길 수 없습니다.")
	private String description;
	
	private MultipartFile imageFile;
	private String defaultImagePath;
	private String savedName;
	
	private List<String> tags;
	
}
