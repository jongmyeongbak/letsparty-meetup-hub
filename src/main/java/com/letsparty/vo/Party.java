package com.letsparty.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Party {
	
	private int no;
	private User leader;
	private Category category;
	private String name;
	private int curCnt;
	private int quota;
	private String status;
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;
	private String description;
	private String filename;

}
