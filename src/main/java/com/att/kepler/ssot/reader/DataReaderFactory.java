package com.att.kepler.ssot.reader;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.att.kepler.ssot.dao.BanDetailCrudOperations;
import com.att.kepler.ssot.util.DataUtil;

public class DataReaderFactory implements ReaderFactory {
	private MongoTemplate mongoOperations;
	private String banCollectionName;
    private ExecutorService executor = Executors.newFixedThreadPool(100);
	
	public DataReaderFactory(MongoTemplate mongoOperations, String banCollectionName) {
		this.mongoOperations = mongoOperations;
		this.banCollectionName = banCollectionName;
	}

	@Override
	public DataReader getDataReader(String filePath) throws IOException {
		return new CSVDataReader(filePath);
	}

	@Override
	public DataWriter getDataWritier() {
		return new DataWriterImpl(new BanDetailCrudOperations(mongoOperations,banCollectionName, DataUtil.BAN_IDENTIFIER),executor);
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}
}
