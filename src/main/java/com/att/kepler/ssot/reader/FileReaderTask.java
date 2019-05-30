package com.att.kepler.ssot.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
	private String backupDir = "";
	private String corruptDir = "";
	private CrudOperations<String, FileInfo> fileOperations;
	private ReaderFactory readerFactory;
    private static final int DEFAULT_BATCH_LIMIT = 1000;

	public FileReaderTask(String outputDir, CrudOperations<String, FileInfo> fileOperations,
			ReaderFactory readerFactory) {
		this.outputDir = outputDir;
		this.fileOperations = fileOperations;
		this.readerFactory = readerFactory;
	}

	public FileReaderTask(String outputDir, String backupDir, CrudOperations<String, FileInfo> fileOperations,
			ReaderFactory readerFactory) {
		this(outputDir, fileOperations, readerFactory);
		this.backupDir = backupDir;
	}

	public FileReaderTask(String outputDir, String backupDir, String corruptDir,
			CrudOperations<String, FileInfo> fileOperations, ReaderFactory readerFactory) {
		this(outputDir, backupDir, fileOperations, readerFactory);
		this.corruptDir = corruptDir;
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
				if (file.isFile()) {
					Query query = Query.query(Criteria.where("ouputFileName").is(file.getName()).and("status")
							.in("IN_PROGRESS", "PROCESSED"));
					if (!fileOperations.exists(query)) {
						processFile(file);
					}
				} else {
					processPendingFiles(file);
				}
			}

		} catch (IOException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void processFile(File file) throws IOException {
		try {
			logger.info("Processing file " + file.getAbsolutePath());
			DataReader<Map> reader = readerFactory.getDataReader(file.getAbsolutePath());
			DataWriter<Map> writer = readerFactory.getDataWritier();
			long startTime = System.currentTimeMillis();
			// File Operation is pending
			FileInfo info = new FileInfo();
			info.setCreatedTimestamp(DataUtil.currentTimestamp());
			info.setOuputFileName(file.getName());
			info.setStatus("IN_PROGRESS");
			info.setDescription("CSV file upload in progress");
			info.setFileModifiedDate(file.lastModified());
			info.setLineCount(reader.numberOfRecords());
			fileOperations.save(info);
			logger.info("File upload in-progress " + info);

			while (reader.hasNext()) {
				reader.bulkRead(writer, DEFAULT_BATCH_LIMIT);
			}
			long endTime = System.currentTimeMillis();
			// save processed file
			info.setStatus("PROCESSED");
			info.setDescription("CSV file uploaded");
			info.setProcessedTimestamp(DataUtil.currentTimestamp());
			info.setLineCount(reader.numberOfRecords());
			info.setNumberOfRecords(reader.numberOfRecords() - 1);
			fileOperations.save(info);
			logger.info("File processed , time taken: " + (endTime - startTime) / 1000 + ", info: " + info);
			DataUtil.moveFile(Paths.get(file.getAbsolutePath()), Paths.get(backupDir, file.getName()));
		} catch (Exception ex) {
			String path = file.getAbsolutePath();
			logger.error("Failed to read file: " + path);
			logger.error(ex.getMessage());
			DataUtil.moveFile(Paths.get(path), Paths.get(corruptDir, file.getName()));
			logger.warn("Bad/corrupted file moved to : " + corruptDir);
		}
	}

}
