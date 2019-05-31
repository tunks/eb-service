package com.att.kepler.ssot.reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import com.att.kepler.ssot.util.DataUtil;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/***
 * 
 * CSV Reader implementation 
 */
public class CSVDataReader implements DataReader<Document>{
	private static final Logger logger = LoggerFactory.getLogger(CSVDataReader.class);
	private CsvParser csvParser ;
	private long numberOfRecords  = 0;
	private Record current;
	private DocumentConverter mapper;
	public CSVDataReader(String filePath) throws IOException {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		csvParser = new CsvParser(settings);
		csvParser.beginParsing(Paths.get(filePath).toFile(), StandardCharsets.UTF_8);
		current = csvParser.parseNextRecord();
		numberOfRecords = (hasNext())? 1: 0;
		mapper = new DocumentConverter(DataUtil.currentTimestamp());
	}

	@Override
	public boolean hasNext() {
		return current != null;
	}

	@Override
	public Document next() {
		return hasNext()? mapper.convert(current.toFieldObjectMap()): null;
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
	public void next(DataBufferWriter writer) {
		try {
			writer.write(next());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void bulkRead(DataBufferWriter writer, int limit) {
		try {
			int count = 0;
			List<Document> items = new ArrayList();
			logger.debug("before bulk read: "+items.size());

			current = csvParser.parseNextRecord();
			while(hasNext()  && count<limit) {
				items.add(mapper.convert(current.toFieldObjectMap()));
				count += 1;
				numberOfRecords +=1;
				current = csvParser.parseNextRecord();
			}
			writer.write(items);
			logger.debug("after bulk read: "+items.size());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void bulkRead(Queue<List<Document>> queue, int limit) {
		try {
			int count = 0;
			List<Document> items = new LinkedList();
			while(hasNext() && count<limit) {
				items.add(next());
				count += 1;
				numberOfRecords +=1;
			}
			queue.add(items);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}		
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
}
