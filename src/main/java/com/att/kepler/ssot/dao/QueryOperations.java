package com.att.kepler.ssot.dao;

import org.springframework.data.mongodb.core.query.Query;

public interface QueryOperations<ID,T> {
	 public T findBy(Query query);
	 public boolean exists(Query query);
     public T find(ID id);
}
