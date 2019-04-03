package com.att.kepler.ssot.dao;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

/***
 * Ban Details Crud Operations
 * 
 *  @author sj204x
 */

public class BanDetailCrudOperations implements CrudOperations<String,Map<String,Object>>{
    private final static UpdateOptions UPDATE_OPTIONS = new UpdateOptions().upsert(true);

    private MongoOperations mongoOperations;
	private String collectionName;
	private String identifier;
	private MongoCollection<Document> collection;

	public BanDetailCrudOperations(MongoOperations mongoOperations, String collectionName, String identifier) {
		super();
		this.mongoOperations = mongoOperations;
		this.collectionName = collectionName;
		this.identifier = identifier;
		this.collection = mongoOperations.getCollection(collectionName);
	}
	
	@Override
	public void save(Map<String,Object> object) {
		Object value = object.get(identifier);
		Document toInsert = new Document();
		 for(Map.Entry<String, Object> entry: object.entrySet()) {
	    	 if(!entry.getKey().equals(identifier)) {
	    	   toInsert.append(entry.getKey(), entry.getValue());
	    	 }
	     }
		 Document upsert = new Document().append("$setOnInsert", toInsert)
									     .append("$set", new Document(identifier, value));

		 collection.updateOne(Filters.eq(identifier, value), upsert, UPDATE_OPTIONS);
	}

	@Override
	public void saveAll(List<Map<String,Object>> objects) {
		List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>();
		for (Map<String,Object> object : objects) {
			 Object value = object.get(identifier);
		     Document doc = new Document();
		     for(Map.Entry<String, Object> entry: object.entrySet()) {
		    	 if(!entry.getKey().equals(identifier)) {
		    	   doc.append(entry.getKey(), entry.getValue());
		    	 }
		     }	     
		     requests.add(new UpdateOneModel<Document>(Filters.eq(identifier, value),new Document("$set", doc), UPDATE_OPTIONS));  
		}
		
		collection.bulkWrite(requests, new BulkWriteOptions().ordered(false));
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
		return findBy(new Query(Criteria.where(identifier).is(id)));
	}

	@Override
	public Map findBy(Query query) {
		return mongoOperations.findOne(query, Map.class,collectionName);
	}

	@Override
	public boolean exists(Query query) {
		return mongoOperations.exists(query, collectionName);
	}

}
