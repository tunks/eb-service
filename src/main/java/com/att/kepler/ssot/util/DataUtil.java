package com.att.kepler.ssot.util;

import java.util.Calendar;
import java.util.Map;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Update;

public class DataUtil {
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
}
