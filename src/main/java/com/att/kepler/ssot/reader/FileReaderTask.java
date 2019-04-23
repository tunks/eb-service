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
				for (File file : listOfFiles) {
					if(file.isFile()) {
						Query query = Query.query(Criteria.where("ouputFileName").is(file.getName())
								                          .and("status").in("IN_PROGRESS","PROCESSED"));
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
			//File Operation is pending
			FileInfo info = new FileInfo();
			info.setCreatedTimestamp(DataUtil.currentTimestamp());
			info.setOuputFileName(file.getName());
			info.setStatus("IN_PROGRESS");
	  		info.setDescription("CSV file upload in progress");
	  		info.setFileModifiedDate(file.lastModified());
			fileOperations.save(info);
			logger.info("File upload in-progress "+info);

			while (reader.hasNext()) {
				reader.bulkRead(writer, 1000);
			}
			long endTime =  System.currentTimeMillis();
			//save processed file
			info.setStatus("PROCESSED");
	  		info.setDescription("CSV file uploaded");
	  		info.setProcessedTimestamp(DataUtil.currentTimestamp());
			fileOperations.save(info);
			logger.info("File processed , time taken: "+(endTime-startTime)/1000 + ", info: "+info);
		} catch (Exception e) {
			logger.error("Failed to process file " + file.getAbsolutePath());
			e.printStackTrace();
		}
	}

}
