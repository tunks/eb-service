package com.att.kepler.ssot.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Update;

public class DataUtil {
	private static final Logger logger = LoggerFactory.getLogger(DataUtil.class);

	  public final static String RECORD_TIMESTAMP = "receivedTimestamp";
	  public final static String BAN_IDENTIFIER = "ban";
	  public final static String CSV_EXTENSION   = ".csv";
      public static long currentTimestamp() {
    	  Calendar calendar = Calendar.getInstance();
    	  return calendar.getTimeInMillis();
      }
      
      
      public static Update objectToUpdate(Map object){
    	  Document doc = new Document(object);
    	  doc.put("_id", object.get(DataUtil.BAN_IDENTIFIER));
  		  return  Update.fromDocument(doc);
      }
      
      
      public static String createFilePathWithDate(String fileName) {
    	     StringBuilder builder = new StringBuilder();
    	     builder.append(fileName);
    	     builder.append("/");
    	     builder.append(currentTimestamp());
    	     builder.append(CSV_EXTENSION);
		     return builder.toString();
      }
      
      /***
       * Move file to destination 
       * 
       * @param source file
       * @param destionation file
       */
      public static void moveFile(Path srcPath,Path destPath) throws IOException{
    	  try {
			Files.move(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
			logger.info("File moved source path: "+srcPath +" , destPath: "+destPath);
		} catch (IOException e) {
			logger.error("Failed to move file source path: "+srcPath +" , destPath: "+destPath);
			throw new IOException(e.getMessage());
		}
      }
}
