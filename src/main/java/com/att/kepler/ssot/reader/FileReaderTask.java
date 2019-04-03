package com.att.kepler.ssot.reader;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.att.kepler.ssot.dao.CrudOperations;
import com.att.kepler.ssot.model.FileInfo;
import com.att.kepler.ssot.util.DataUtil;

public class FileReaderTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(FileReaderTask.class);
	private String outputDir;
	private CrudOperations<String, FileInfo> fileOperations;
	private ReaderFactory readerFactory;
	public FileReaderTask(String outputDir, CrudOperations<String, FileInfo> fileOperations,
			ReaderFactory readerFactory) {
		this.outputDir = outputDir;
		this.fileOperations = fileOperations;
		this.readerFactory = readerFactory;
	}

	@Override
	public void run() {
		try {
			File folder = new File(outputDir);
			processPendingFiles(folder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processPendingFiles(File root) {
		try {
				File[] listOfFiles = root.listFiles();
				String filePath;
				for (File file : listOfFiles) {
					filePath = file.getAbsolutePath();
					if(file.isFile()) {
						Query query = Query.query(Criteria.where("filePath").is(filePath));
					    if(!fileOperations.exists(query)) {
							processFile(file);
					    }
					}
					else {
						processPendingFiles(file);
					} 	
				}
		
		}catch(Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void processFile(File file) {
		try {
			logger.info("Processing file "+file.getAbsolutePath());
			DataReader<Map> reader = readerFactory.getDataReader(file.getAbsolutePath());
			DataWriter<Map> writer = readerFactory.getDataWritier();
			long startTime = System.currentTimeMillis();
			while (reader.hasNext()) {
				reader.bulkRead(writer, 1000);
			}
			long endTime =  System.currentTimeMillis();
			FileInfo info = new FileInfo();
			info.setFilePath(file.getAbsolutePath());
			info.setStatus("PROCESSED");
	  		info.setDescription("Output");
			info.setCreatedTimestamp(DataUtil.currentTimestamp());
			fileOperations.save(info);
			logger.info("File processed , time taken: "+(endTime-startTime)/1000);
		} catch (Exception e) {
			logger.error("Failed to process file " + file.getAbsolutePath());
			e.printStackTrace();
		}
	}

}
