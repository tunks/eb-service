package com.att.kepler.ssot.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import com.att.kepler.ssot.util.DataUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentSaveOperationsTest {
	@Autowired
	@Qualifier("mongoTemplate")
    private MongoTemplate mongoOperations;
	
	private String BAN_COLLECTION  ="ban_test";
	private SaveOperations<Document> saveOperations;
	
	@Before
	public void setup() {
		saveOperations = new DocumentSaveOperations(this.mongoOperations,BAN_COLLECTION,DataUtil.BAN_IDENTIFIER);
	}
	
	@Test
	public void testSaveAndFind() {
		String id = UUID.randomUUID().toString();
		Document doc = new Document();
		doc.put(DataUtil.BAN_IDENTIFIER, id);
		doc.put("description", "testing echo ban");
		saveOperations.save(doc);
		Query query = new Query(Criteria.where(DataUtil.BAN_IDENTIFIER).is(id));
		Map result = mongoOperations.findOne(query, Map.class, BAN_COLLECTION);
		assertNotNull(result);
	}

	@Test
	public void testSaveAllBulkInsertAndFind() {
		List<Document> docs = new ArrayList();
		int size = 10000;
		for(int i =0 ; i< size; i++) {	
		  docs.add(createObject(String.valueOf(i)));
		}
		long startTime = System.currentTimeMillis();
		saveOperations.saveAll(docs);
		long endTime = System.currentTimeMillis();
        System.out.println(size+" records, time taken : "+(endTime-startTime)/1000);
		
		docs.stream().forEach(obj->{
			String id = (String)obj.get(DataUtil.BAN_IDENTIFIER);
			Query query = new Query(Criteria.where(DataUtil.BAN_IDENTIFIER).is(id));
			Map result = mongoOperations.findOne(query, Map.class, BAN_COLLECTION);
			assertNotNull(result);
		});
	}
	
	private Document createObject(String id) {
		Map<String,Object> object = new HashMap();
		object.put(DataUtil.BAN_IDENTIFIER, id);
		object.put("description", "testing echo ban");
		return new Document(object);
	}

}
