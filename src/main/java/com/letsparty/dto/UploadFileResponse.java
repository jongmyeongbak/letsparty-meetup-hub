package com.letsparty.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadFileResponse {

	private String fileName;
	private String savedName;
	private String fileDownloadUri;
	private String fileType;
	private long size;
	
	public UploadFileResponse(String fileName, String savedName, String fileDownloadUri, String fileType, long size) {
		super();
		this.fileName = fileName;
		this.savedName = savedName;
		this.fileDownloadUri = fileDownloadUri;
		this.fileType = fileType;
		this.size = size;
	}
}
