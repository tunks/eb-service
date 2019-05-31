package com.att.kepler.ssot.reader;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * Data reader  
 *  
 */
public interface DataReader<T>  extends Iterator<T>{
      public List<T> bulkRead(int limit);
      public void next(DataBufferWriter writer);
      public void bulkRead(DataBufferWriter writer, int limit);
      public void bulkRead(Queue<List<T>> queue, int limit);
      public long numberOfRecords();
}
