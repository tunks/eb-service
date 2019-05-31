package com.att.kepler.ssot.reader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.kepler.ssot.workers.WorkerPool;

/***
 * DataBuffer writer implementation -- writes list of documents to queue buffer for processing 
 * 
 */
public class DataBufferWriterImpl implements DataBufferWriter<Document>{
	private static final Logger logger = LoggerFactory.getLogger(DataBufferWriterImpl.class);
    private Queue<List<Document>> dataQueue;

	public DataBufferWriterImpl(Queue<List<Document>> dataQueue) {
		this.dataQueue = dataQueue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void write(Document object) throws Exception {
		try {
		  dataQueue.add(Arrays.asList(object));	
		}catch(Exception ex) {
			logger.error("Failed to write data to queue buffer");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void write(List<Document> objects) throws Exception {
		try {
		  dataQueue.add(objects);
		}
		catch(Exception ex) {
			logger.error("Failed to write data to queue buffer");
		}
	}	
}
