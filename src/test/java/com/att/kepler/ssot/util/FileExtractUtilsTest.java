//package com.att.kepler.ssot.util;
//
//import static org.junit.Assert.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Calendar;
//
//import org.apache.commons.compress.archivers.ArchiveException;
//import org.junit.Before;
//import org.junit.Test;
//
//public class FileExtractUtilsTest {
//	File sourceDir;
//	File outputDir;
//	@Before
//	public void setup() throws IOException, ArchiveException {
//	    Calendar calendar = Calendar.getInstance();
//		//sourceDir = new File("/Users/sj204x/Documents/development/data/ecoban/inputs/eco_bans_details.csv.gz");
//		sourceDir = new File("/Users/sj204x/Documents/development/data/ecoban/inputs/eco_bans_details.csv.gz");
//        
//		String fileName = "eco_bans_"+calendar.getTimeInMillis() + ".csv";
//		outputDir = new File("/Users/sj204x/Documents/development/data/ecoban/outputs/"+fileName);
//		
//		System.out.println("Source file :"+sourceDir.getAbsolutePath());
//	}
//	
//	@Test
//	public void testExtractZip() throws ArchiveException {
//		if(sourceDir.getAbsolutePath().endsWith(".zip")) {
//		    FileExtractUtils.extractZip(sourceDir, outputDir);
//		}
//		else if(sourceDir.getAbsolutePath().endsWith(".gz")) {
//			FileExtractUtils.extractGZip(sourceDir, outputDir);
//	     }
//	}
//
//	
//	
//	
//    
//    /*@Test
//    public void add_all_files_from_a_directory_to_a_zip_archive() throws Exception {
//        File source = new File("build/resources/test");
//        File destination = new File("build/resources.zip");
//        destination.delete();
//
//        addFilesToZip(source, destination);
//
//        assertTrue("Expected to find the zip file ", destination.exists());
//        assertZipContent(destination);
//    }
//    */
//
//}
