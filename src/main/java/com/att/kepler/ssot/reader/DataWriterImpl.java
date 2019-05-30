package com.att.kepler.ssot.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.att.kepler.ssot.dao.CrudOperations;

public class DataWriterImpl implements DataWriter<Map<String,Object>>{
	private static final Logger logger = LoggerFactory.getLogger(DataWriterImpl.class);
    private CrudOperations<String,Map<String,Object>> dataOperations;
    private ExecutorService executor;
    public DataWriterImpl(CrudOperations<String,Map<String,Object>>  dataOperations) {
		this.dataOperations = dataOperations;
	}
	public DataWriterImpl(CrudOperations<String,Map<String,Object>>  dataOperations, ExecutorService executor) {
		this.dataOperations = dataOperations;
		this.executor  = executor;
	}

	@Override
	public void write(Map<String,Object> object) throws Exception {
		dataOperations.save(object);
	}

	@Override
	public void write(List<Map<String,Object>> objects) throws Exception {
		//dataOperations.saveAll(objects);		
		executor.execute(new BatchDataWriterTask(objects));	
	}
	
	private  class BatchDataWriterTask implements Runnable{
		private List<Map<String, Object>> batch;
		
		public BatchDataWriterTask(List<Map<String, Object>> batch) {
			this.batch = batch;
		}

		@Override
		public void run() {
			logger.info("Run writer batch executor task");
			try {
			    dataOperations.saveAll(batch);		
			}
			catch(Exception ex) {
				logger.error("Executor writer task error "+ex.getMessage());
				ex.printStackTrace();
			}
		}
		
	}
}
