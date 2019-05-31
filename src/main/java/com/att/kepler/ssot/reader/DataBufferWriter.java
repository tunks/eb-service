package com.att.kepler.ssot.reader;

import java.util.List;

/**
 * 
 * Base DataBuffer writer 
 */
public interface DataBufferWriter<T> {
	/***
	 * Write to buffer 
	 * 
	 * @param object: T
	 */
    public void write(T object) throws Exception;
    /**
     * Writes list of data to buffer
     * 
     *  @param objects: List<T>
     **/
    public void write(List<T> objects) throws Exception;
}
