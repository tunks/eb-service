package com.att.kepler.ssot.reader;

import java.util.List;

public interface DataWriter<T> {
    public void write(T object) throws Exception;
    public void write(List<T> object) throws Exception;
}
