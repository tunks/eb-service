package com.att.kepler.ssot.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.att.kepler.ssot.dao.CrudOperations;
import com.att.kepler.ssot.dao.FileInfoCrudOperations;
import com.att.kepler.ssot.model.FileInfo;
import com.att.kepler.ssot.reader.DataReaderFactory;
import com.att.kepler.ssot.reader.FileExtractorTask;
import com.att.kepler.ssot.reader.FileReaderTask;
import com.att.kepler.ssot.reader.ReaderFactory;

@Service("dataService")
public class DataService implements InitializingBean{
	private static final Logger logger = LoggerFactory.getLogger(DataService.class);

	private ScheduledExecutorService schuduleExecutor;
	
	@Autowired
	@Qualifier("mongoTemplate")
	private MongoTemplate mongoOperations;
	
	@Value("${schedule.threadPool:10}")
	private int threadPool;
	
	@Value("${schedule.interval:1000}")
	private long timeInterval;
	
	@Value("${file.input.dir}")
	private String inputDir;
	
	@Value("${file.input.dir.bk}")
	private String inputDirBackup;
	
	@Value("${file.output.dir}")
	private String outputDir;
	
	
	@Value("${file.output.dir.bk}")
	private String outputDirBackup;
	
	@Value("${ban.collection: ban_detail}")
	private String banCollectionName;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Starting file scheduled tasks , time interval "+timeInterval);
		schuduleExecutor = Executors.newScheduledThreadPool(threadPool);
	    CrudOperations<String,FileInfo> fileOperations = new  FileInfoCrudOperations(mongoOperations);

		ReaderFactory readerFactory = new DataReaderFactory(mongoOperations,banCollectionName);
		schuduleExecutor.scheduleWithFixedDelay(new FileExtractorTask(inputDir,outputDir,inputDirBackup,fileOperations), 0, timeInterval, TimeUnit.MILLISECONDS);
		schuduleExecutor.scheduleWithFixedDelay(new FileReaderTask(outputDir, outputDirBackup,fileOperations,readerFactory), 0, timeInterval, TimeUnit.MILLISECONDS);
	}

}
