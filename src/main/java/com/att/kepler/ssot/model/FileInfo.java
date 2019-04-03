package com.att.kepler.ssot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FileInfo {
	@Id
	private String id;
	private long createdTimestamp;
	private long fileModifiedDate;
	private String filePath;
	private String status; // PROCESSED, PENDING

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(long createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public long getFileModifiedDate() {
		return fileModifiedDate;
	}

	public void setFileModifiedDate(long fileModifiedDate) {
		this.fileModifiedDate = fileModifiedDate;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String fileName) {
		this.filePath = fileName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", createdTimestamp=" + createdTimestamp + ", fileModifiedDate="
				+ fileModifiedDate + ", filePath=" + filePath + ", status=" + status + "]";
	}

}
