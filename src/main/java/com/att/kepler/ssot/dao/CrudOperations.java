package com.att.kepler.ssot.dao;


/**
 * Base CRUD operations 
 */
public interface CrudOperations<ID,T> extends QueryOperations<ID,T>, SaveOperations<T>, DeleteOperations<ID> {

}
