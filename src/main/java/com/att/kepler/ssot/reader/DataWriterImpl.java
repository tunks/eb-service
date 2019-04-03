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
	private BlockingQueue<Map> dataQueue = new LinkedBlockingDeque();
    private CrudOperations<String,Map<String,Object>> dataOperations;
    private List<Runnable> tasks = new ArrayList();
    private int numberOfTasks = 20;
    private ExecutorService executor;

	public DataWriterImpl(CrudOperations<String,Map<String,Object>>  dataOperations, ExecutorService executor) {
		this.dataOperations = dataOperations;
		this.executor  = executor;
		initializeTasks();
	}

	@Override
	public void write(Map<String,Object> object) throws Exception {
		 dataQueue.offer(object);
	}

	@Override
	public void write(List<Map<String,Object>> objects) throws Exception {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
				   dataOperations.saveAll(objects);		
				}
				catch(Exception ex) {
					logger.error("Failed to write "+ex.getMessage());
					ex.printStackTrace();
				}
			}	
		});	
	}
	
	private void initializeTasks() {
		for(int i = 0 ; i< numberOfTasks; i++) {
		  tasks.add(new DataWriterTask());
		}
		
		handleDataQueue(dataQueue);
	}
	
	private void handleDataQueue(BlockingQueue<Map> dataQueue) {
		tasks.stream().forEach(task->{
			executor.execute(task);	
		});
		
	}
	
	private  class DataWriterTask implements Runnable{
		@Override
		public void run() {
			logger.info("Run writer executor task");
			try {
				Map data;
				while((data = dataQueue.poll(5000, TimeUnit.MILLISECONDS)) != null) {
					dataOperations.save(data);
				}	
			}
			catch(Exception ex) {
				logger.error("Executor writer task error "+ex.getMessage());
				ex.printStackTrace();
			}
		}
		
	}
}
