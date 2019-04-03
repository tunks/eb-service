package com.att.kepler.ssot.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.att.kepler.ssot.model.FileInfo;


/**
 * FileInfo Crud operations to maintain metadata information
 * 
 *  @author sj204x
 */
public class FileInfoCrudOperations implements CrudOperations<String,FileInfo>{
    private MongoOperations mongoOperations;	
	
	public FileInfoCrudOperations(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	@Override
	public void save(FileInfo object) {
		mongoOperations.save(object);	
	}

	@Override
	public void saveAll(List<FileInfo> objects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(List<String> id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FileInfo find(String id) {
		return this.mongoOperations.findOne(new Query(Criteria.where("_id").is(id)), FileInfo.class);
	}

	@Override
	public FileInfo findBy(Query query) {
		return this.mongoOperations.findOne(query,FileInfo.class);
	}

	@Override
	public boolean exists(Query query) {
		return  this.mongoOperations.exists(query,FileInfo.class);
	}

	
}
