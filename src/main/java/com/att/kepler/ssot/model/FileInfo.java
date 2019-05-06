package com.att.kepler.ssot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FileInfo {
	@Id
	private String id;
	private long createdTimestamp;
	private long fileModifiedDate;
	private String originalFileName; // File from original source -- Data lake
	private String status; // EXTRACTED, PROCESSED, PENDING
	private String description; //Source , output -> uploaded
	private String ouputFileName; //Extracted csv file being uploaded
	private long processedTimestamp;
	private long lineCount;
	private long numberOfRecords;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getProcessedTimestamp() {
		return processedTimestamp;
	}

	public void setProcessedTimestamp(long processedTimestamp) {
		this.processedTimestamp = processedTimestamp;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getOuputFileName() {
		return ouputFileName;
	}

	public void setOuputFileName(String ouputFileName) {
		this.ouputFileName = ouputFileName;
	}

	public long getLineCount() {
		return lineCount;
	}

	public void setLineCount(long lineCount) {
		this.lineCount = lineCount;
	}

	public long getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", createdTimestamp=" + createdTimestamp + ", fileModifiedDate="
				+ fileModifiedDate + ", originalFileName=" + originalFileName + ", status=" + status + ", description="
				+ description + ", ouputFileName=" + ouputFileName + ", processedTimestamp=" + processedTimestamp + "]";
	}
}
