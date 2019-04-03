//package com.att.kepler.ssot.reader;
//
//import static org.junit.Assert.*;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//
//public class CSVDataReaderTest {
//	private String CSV_FILE = "/Users/sj204x/Documents/development/data/ecoban/outputs/eco_bans_1554270716531.csv";
//    private CSVDataReader csvDataReader;
//    
//    @Before
//    public void setup() throws IOException {
//		csvDataReader = new CSVDataReader(CSV_FILE);
//    }
//    
//	@Test
//	public void testNext() {
//		Map result = csvDataReader.next();
//		assertNotNull(result);
//		System.out.println("Next result: "+result);
//	}
//
//	@Test
//	public void testBulkRead() {
//		List<Map> result = csvDataReader.bulkRead(20);
//		assertNotNull(result);
//		System.out.println("Next bulk result: "+result);
//	}
//
//}
