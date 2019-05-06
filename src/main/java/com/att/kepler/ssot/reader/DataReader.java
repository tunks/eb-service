package com.att.kepler.ssot.reader;

import java.util.Iterator;
import java.util.List;

/**
 * Data reader  
 *  
 */
public interface DataReader<T>  extends Iterator<T>{
      public List<T> bulkRead(int limit);
      public void next(DataWriter writer);
      public void bulkRead(DataWriter writer, int limit);
      public long numberOfRecords();
}
