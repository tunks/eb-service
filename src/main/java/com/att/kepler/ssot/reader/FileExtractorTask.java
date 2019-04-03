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
		logger.info("task started");
		try {
			/*Supplier<Stream<Path>> paths = () -> Stream.of(Paths.get(inputDir));
			//logger.info("task paths "+paths.collect(Collectors.toList()));
			paths.get()//.filter(Files::isRegularFile)
		           .forEach(f->{
		        	    logger.info("file: "+f.toFile().getName());
		        	  FileInfo  info= fileOperations.findBy( Query.query(Criteria.where("filename").is(f.getFileName())));
		        	  if(info == null) {  
			             File file = f.toFile();
		        		 info = new FileInfo();
		        		 info.setFilePath(file.getAbsolutePath());
		        		 String outputFileName = DataUtil.createFilePathWithDate(outputDir);
		        		 File outputPath = new File(outputFileName);
		        		 FileExtractUtils.extractFile(file, outputPath);
		        		 logger.info("File: "+file.getAbsolutePath() +" , successfully extracted , output: " + outputPath.getAbsolutePath());
		        	  }
		           });
		           */
			File folder = new File(inputDir);
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
			    if (file.isFile()) {
			        logger.info(file.getName());
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
		Query query = Query.query(Criteria.where("filePath").is(sourcePath));
		FileInfo  info = fileOperations.findBy(query);
		logger.info("mongo query "+query.toString());
	  	  if(info == null) {  
	  		 info = new FileInfo();
	  		 info.setFilePath(sourcePath);
	  		 String outputFileName = (sourcePath.endsWith(".zip"))? outputDir: DataUtil.createFilePathWithDate(outputDir);
	  		 logger.info("Output filename: "+outputFileName);
	  		 File outputPath = new File(outputFileName);
	  		 FileExtractUtils.extractFile(file, outputPath);
	  		 fileOperations.save(info);
	  		 logger.info("File: "+sourcePath +" , successfully extracted , output: " + outputPath.getAbsolutePath());
	  	  }
	}

}
