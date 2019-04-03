package com.att.kepler.ssot.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.BasicBSONObject;
import org.bson.Document;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.DefaultIndexOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.QueryMapper;
import org.springframework.data.mongodb.core.convert.UpdateMapper;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.att.kepler.ssot.util.DataUtil;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;

/***
 * Ban Details Crud Operations
 * 
 *  @author sj204x
 */

public class BanDetailCrudOperations implements CrudOperations<String,Map<String,Object>>{
    private MongoOperations mongoOperations;
	private String collectionName;
	private String identifier;
	
    
	public BanDetailCrudOperations(MongoOperations mongoOperations, String collectionName, String identifier) {
		super();
		this.mongoOperations = mongoOperations;
		this.collectionName = collectionName;
		this.identifier = identifier;
	}

	@Override
	public void save(Map<String,Object> object) {
		Object value = object.get(identifier);
		Query query = new Query(Criteria.where(identifier).is(value));
		Update update = new Update();
		update.set(identifier, value);
	     for(Map.Entry<String, Object> entry: object.entrySet()) {
	    	 if(!entry.getKey().equals(identifier)) {
	    	   update.setOnInsert(entry.getKey(), entry.getValue());
	    	 }
	     }
		mongoOperations.upsert(query, update,collectionName);	
	}

	@Override
	public void saveAll(List<Map<String,Object>> objects) {
		BulkOperations ops = mongoOperations.bulkOps(BulkMode.UNORDERED, collectionName);
		for (Map<String,Object> object : objects) {
			 Object value = object.get(identifier);
		     Update update = new Update();
		     update.set(identifier, value);
		     for(Map.Entry<String, Object> entry: object.entrySet()) {
		    	 if(!entry.getKey().equals(identifier)) {
		    	   update.setOnInsert(entry.getKey(), entry.getValue());
		    	 }
		     }
		     ops.upsert(new Query(Criteria.where(identifier).is(value)), update);
		   
		}
		ops.execute();  
	}

	@Override
	public void delete(String id) {
		mongoOperations.remove(new Query(Criteria.where(identifier).is(id)), collectionName);		
	}

	@Override
	public void deleteAll(List<String> ids) {
		ids.stream().forEach(id->{
			delete(id);
		});
	}

	@Override
	public Map find(String id) {
		return mongoOperations.findOne(new Query(Criteria.where(identifier).is(id)), Map.class,collectionName);
	}

	@Override
	public Map findBy(Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Query query) {
		// TODO Auto-generated method stub
		return false;
	}

}
