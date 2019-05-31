package com.att.kepler.ssot.reader;

import java.io.IOException;
import java.util.Queue;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.att.kepler.ssot.dao.DocumentSaveOperations;
import com.att.kepler.ssot.dao.SaveOperations;
import com.att.kepler.ssot.util.DataUtil;
import com.att.kepler.ssot.workers.DefaultWorkerPool;
import com.att.kepler.ssot.workers.Worker;
import com.att.kepler.ssot.workers.WorkerImpl;
import com.att.kepler.ssot.workers.WorkerPool;

public class DataReaderFactory implements ReaderFactory {
	private MongoTemplate mongoOperations;
	private String banCollectionName;
    private Queue<List<Document>> dataQueue = new ConcurrentLinkedQueue();	   
    private WorkerPool<String, Worker> workerPool;
    private int workerCounter = 0;
    private SaveOperations<Document> saveOperations;
    
	public DataReaderFactory(MongoTemplate mongoOperations, String banCollectionName) {
		this.mongoOperations = mongoOperations;
		this.banCollectionName = banCollectionName;
		this.saveOperations = new DocumentSaveOperations(mongoOperations,banCollectionName,DataUtil.BAN_IDENTIFIER);
	}

	@Override
	public DataReader getDataReader(String filePath) throws IOException {
		return new CSVDataReader(filePath);
	}

	@Override
	public DataBufferWriter getDataWritier() {
		return new DataBufferWriterImpl(dataQueue);
	}

	@Override
	public WorkerPool<String, Worker> dataWriterWorkerPool() {
		setupWriterWorkerPool(5);
		return workerPool;
	}
	
	private WorkerPool<String, Worker> setupWriterWorkerPool(int poolSize) {
		if(workerPool == null) {
		   workerPool = new DefaultWorkerPool(poolSize);	 
		 }
		return workerPool;
	}
	
	@Override
	public WorkerPool<String, Worker> dataWriterWorkerPool(int numberOfWorkers) {
		setupWriterWorkerPool(numberOfWorkers);
		for(int i = 0; i< numberOfWorkers; i++) {
		  workerPool.execute(createWorker());
		}
		return workerPool;
	}

	@Override
	public Worker createWorker() {
		workerCounter++;
		String workerId =  new StringBuilder().append("worker_")
				                              .append(workerCounter)
				                              .toString();
		return new WorkerImpl(workerId,dataQueue,saveOperations);
	}

}
