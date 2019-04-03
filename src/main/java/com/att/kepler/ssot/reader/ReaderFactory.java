package com.att.kepler.ssot.reader;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

public interface ReaderFactory {
	 public DataReader getDataReader(String filePath) throws IOException;
	 
	 public DataWriter getDataWritier();
	 
	 public Executor getExecutor();
}
