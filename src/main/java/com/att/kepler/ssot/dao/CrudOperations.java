package com.att.kepler.ssot.dao;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;

/**
 * Base CRUD operations 
 */
public interface CrudOperations<ID,T> {
	 public T findBy(Query query);
	 public boolean exists(Query query);
     public T find(ID id);
     public void save(T object);
     public void saveAll(List<T> objects);
     public void delete(ID id);
     public void deleteAll(List<ID> id);
}
