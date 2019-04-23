package com.att.kepler.ssot.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.att.kepler.ssot.dao.CrudOperations;
import com.att.kepler.ssot.model.FileInfo;
import com.att.kepler.ssot.util.DataUtil;
import com.att.kepler.ssot.util.FileExtractUtils;

public class FileExtractorTask implements Runnable{
	private static final Logger logger = LoggerFactory.getLogger(FileExtractorTask.class);
	private String inputDir ="";
	private String outputDir = "";
	private CrudOperations<String,FileInfo> fileOperations;
	
	public FileExtractorTask(String inputDir, String outputDir,  CrudOperations<String,FileInfo> fileOperations) {
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.fileOperations = fileOperations;
	}
 
	@Override
	public void run() {
		try {
			File folder = new File(inputDir);
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
			    if (file.isFile()) {
			        extractFile(file);
			    }
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 			
	}
	
	
	private void extractFile(File file) {
 	    String sourcePath =  file.getAbsolutePath();
		Query query = Query.query(Criteria.where("originalFileName").is(file.getName()));
	  	  if(!fileOperations.exists(query)) { 
		     logger.info("Extracting file: "+file.getName());
	  		 FileInfo info = new FileInfo();
	  		 info.setOriginalFileName(file.getName());
	  		 info.setFileModifiedDate(file.lastModified());
	  		 info.setCreatedTimestamp(DataUtil.currentTimestamp());
			 info.setStatus("EXTRACTED");
	  		 info.setDescription("Source");
	  		 String outputFileName = (sourcePath.endsWith(".zip"))? outputDir: DataUtil.createFilePathWithDate(outputDir);
	  		 File outputPath = new File(outputFileName);
	  		 FileExtractUtils.extractFile(file, outputPath);
	  		 info.setProcessedTimestamp(DataUtil.currentTimestamp());
	  		 info.setOuputFileName(outputPath.getName());
	  		 fileOperations.save(info);
	  		 logger.info("File: "+sourcePath +" , successfully extracted , output: " + outputPath.getAbsolutePath());
	  	  }
	}

}
