package com.att.kepler.ssot.dao;

import java.util.List;

public interface SaveOperations<T> {
    public void save(T object);
    public void saveAll(List<T> objects);
}
