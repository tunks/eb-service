package com.att.kepler.ssot.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import com.att.kepler.ssot.util.DataUtil;

public class CSVDataReader2 implements DataReader<Document>{
	private static final Logger logger = LoggerFactory.getLogger(CSVDataReader2.class);
	private CSVParser csvParser ;
	private Iterator<CSVRecord> csvIterator;
	private DocumentConverter mapper;
	private long numberOfRecords  = 0;
	public CSVDataReader2(String filePath) throws IOException {
		csvParser = new CSVParser(Files.newBufferedReader(Paths.get(filePath)), CSVFormat.EXCEL
		                .withFirstRecordAsHeader()
		                .withIgnoreHeaderCase()
		                .withTrim());
		csvIterator = csvParser.iterator();
		mapper = new DocumentConverter(DataUtil.currentTimestamp());
	}

	@Override
	public boolean hasNext() {
		return csvIterator.hasNext();
	}

	@Override
	public Document next() {
		return mapper.convert(csvIterator.next().toMap());
	}

	@Override
	public List<Document> bulkRead(int limit) {
		int count = 0;
		List<Document> records = new ArrayList<Document>();
		while(hasNext() && count<limit) {
			records.add(next());
			count += 1;
			numberOfRecords +=1;
		}
		return records;
	}


	@Override
	public long numberOfRecords() {
	   return numberOfRecords;
	}	
	
	private class DocumentConverter implements Converter<Map,Document> {
        private long timestamp;

		public DocumentConverter( long timestamp) {
			this.timestamp = timestamp;
		}

		@Override
		public Document convert(Map record) {
            return new Document(record).append(DataUtil.RECORD_TIMESTAMP, timestamp);
		}
	}

	@Override
	public void next(DataBufferWriter writer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bulkRead(DataBufferWriter writer, int limit) {
		try {
			int count = 0;
			List<Document> items = new ArrayList();
			logger.debug("before bulk read: "+items.size());
			while(hasNext()  && count<limit) {
				try {
				  items.add(next());
				  numberOfRecords +=1;
				}catch(Exception ex) {
				   logger.error("Error parsing csv line #: "+numberOfRecords + ", error_msg: "+ex.getMessage());
				}
				count += 1;
			}
			writer.write(items);
			logger.debug("after bulk read: "+items.size());
		}
		catch(Exception ex) {
			logger.error("Error parsing csv : "+ex.getMessage());
		}
		
	}

	@Override
	public void bulkRead(Queue<List<Document>> queue, int limit) {
		// TODO Auto-generated method stub
		
	}	
}
