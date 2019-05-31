package com.att.kepler.ssot.reader;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

import com.att.kepler.ssot.workers.Worker;
import com.att.kepler.ssot.workers.WorkerPool;

public interface ReaderFactory {
	 public DataReader getDataReader(String filePath) throws IOException;
	 public DataBufferWriter getDataWritier();
	 public WorkerPool<String, Worker> dataWriterWorkerPool();
	 public WorkerPool<String, Worker> dataWriterWorkerPool(int numberOfWorkers);
	 public Worker createWorker();
}
