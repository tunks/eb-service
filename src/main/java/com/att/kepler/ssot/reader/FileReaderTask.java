package com.att.kepler.ssot.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		logger.info("tasks");
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
					Query query = Query.query(Criteria.where("filePath").is(filePath));
					boolean exits = fileOperations.exists(query);
				    logger.info(file.getName() +", query: "+query);
				    if(!exits) {
						processFile(file);
				    }
				    else {
				    	logger.info("Exists ");
				    }
				    
						
				}
		
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void processFile(File file) {
		try {
			logger.info("processing file "+file.getAbsolutePath());
			DataReader<Map> reader = readerFactory.getDataReader(file.getAbsolutePath());
			DataWriter<Map> writer = readerFactory.getDataWritier();
			logger.info("processing file hasNext");
			while (reader.hasNext()) {
				reader.next(writer);
			}
			logger.info("processing file hasNext");
			FileInfo info = new FileInfo();
			info.setFilePath(file.getAbsolutePath());
			info.setStatus("PROCESSED");
			info.setCreatedTimestamp(DataUtil.currentTimestamp());
			fileOperations.save(info);
			logger.info("File processed +");
		} catch (Exception e) {
			logger.error("Failed to process file " + file.getAbsolutePath());
			e.printStackTrace();
		}
	}

}
