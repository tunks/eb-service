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
import org.springframework.core.convert.converter.Converter;

import com.att.kepler.ssot.util.DataUtil;

public class CSVDataReader implements DataReader<Map>{
	private CSVParser csvParser ;
	private Iterator<CSVRecord> csvIterator;
	private Converter<CSVRecord,Map> converter;
	public CSVDataReader(String filePath) throws IOException {
		csvParser = new CSVParser(Files.newBufferedReader(Paths.get(filePath)), CSVFormat.DEFAULT
		                .withFirstRecordAsHeader()
		                .withIgnoreHeaderCase()
		                .withTrim());
		csvIterator = csvParser.iterator();
		this.converter = new MapConverter(csvParser.getHeaderMap(), DataUtil.currentTimestamp());
	}

	@Override
	public boolean hasNext() {
		return csvIterator.hasNext();
	}

	@Override
	public Map next() {
		return converter.convert(csvIterator.next());
	}

	@Override
	public List<Map> bulkRead(int limit) {
		int count = 0;
		List<Map> records = new ArrayList<Map>();
		while(hasNext() && count<limit) {
			records.add(next());
			count += 1;
		}
		return records;
	}

	@Override
	public void next(DataWriter writer) {
		try {
			writer.write(next());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void bulkRead(DataWriter writer, int limit) {
		int count = 0;
		while(hasNext() && count<limit) {
			next(writer);
			count += 1;
		}
		
	}
	
	private class MapConverter implements Converter<CSVRecord,Map> {
        private Map<String,Integer> headerMap;
        private long timestamp;
		public MapConverter(Map<String, Integer> headerMap) {
			this.headerMap = headerMap;
		}

		public MapConverter(Map<String, Integer> headerMap, long timestamp) {
			this.headerMap = headerMap;
			this.timestamp = timestamp;
		}

		@Override
		public Map convert(CSVRecord record) {
            Map result = record.toMap();
            /*Object value;
            for(Entry<String, Integer> entry: headerMap.entrySet()) {
            	value = record.get(entry.getValue());
            	if(value != null) {
            		
            	}
            	record.to
            }
            */
            //map the cvs parser timestamp to the csv record
            result.put(DataUtil.RECORD_TIMESTAMP, timestamp);
			return result;
		}
	}

	
}
