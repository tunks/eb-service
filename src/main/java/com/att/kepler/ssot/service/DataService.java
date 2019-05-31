package com.att.kepler.ssot.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
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
import com.att.kepler.ssot.workers.Worker;
import com.att.kepler.ssot.workers.WorkerPool;

@Service("dataService")
public class DataService implements InitializingBean, DisposableBean{
	private static final Logger logger = LoggerFactory.getLogger(DataService.class);

	private ScheduledExecutorService schuduleExecutor1;
	private ScheduledExecutorService schuduleExecutor2;

	
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
	
	@Value("${file.corrupt.dir}")
	private String corruptDir;
	
	@Value("${ban.collection: ban_detail}")
	private String banCollectionName;
	
	@Value("${worker.pool.size: 50}")
	private int numberOfWorkers;
	
	private ReaderFactory readerFactory;
	private WorkerPool<String, Worker> workerPool;
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Starting file scheduled tasks , time interval "+timeInterval +"");
		schuduleExecutor1 = Executors.newSingleThreadScheduledExecutor();
		schuduleExecutor2 = Executors.newSingleThreadScheduledExecutor();
	    CrudOperations<String,FileInfo> fileOperations = new  FileInfoCrudOperations(mongoOperations);
	    readerFactory = new DataReaderFactory(mongoOperations,banCollectionName);
	    workerPool = readerFactory.dataWriterWorkerPool(numberOfWorkers);
		Runnable extratorTask = new FileExtractorTask(inputDir,outputDir,inputDirBackup,corruptDir,fileOperations);
		Runnable readerTask = new FileReaderTask(outputDir, outputDirBackup,corruptDir,fileOperations,readerFactory);
		//schuduleExecutor1.scheduleWithFixedDelay(extratorTask, 0, timeInterval, TimeUnit.MILLISECONDS);
		schuduleExecutor2.scheduleWithFixedDelay(readerTask, 0, timeInterval, TimeUnit.MILLISECONDS);
	}

	@Override
	public void destroy() throws Exception {
		logger.info("Terminating executor schedulers");
		schuduleExecutor1.shutdown();
		schuduleExecutor2.shutdown();	
	}

}
