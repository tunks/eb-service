package com.att.kepler.ssot.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.att.kepler.ssot.util.DataUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BanDetailCrudOperationsTest {
	@Autowired
	@Qualifier("mongoTemplate")
    private MongoTemplate mongoOperations;
	
	private String BAN_COLLECTION  ="ban_test";
	private BanDetailCrudOperations crudOperations;
	
	@Before
	public void setup() {
		crudOperations = new BanDetailCrudOperations(this.mongoOperations,BAN_COLLECTION,DataUtil.BAN_IDENTIFIER);
	}
	
	@Test
	public void testSaveAndFind() {
		String id = UUID.randomUUID().toString();
		Map<String,Object> object = new HashMap();
		object.put(DataUtil.BAN_IDENTIFIER, id);
		object.put("description", "testing echo ban");
		crudOperations.save(object);
		
		Map result = crudOperations.find(id);
		assertNotNull(result);
	}

	@Test
	public void testSaveAllBulkInsertAndFind() {
		List<Map<String,Object>> objects = new ArrayList();
		int size = 10000;
		for(int i =0 ; i< size; i++) {	
		  objects.add(createObject(String.valueOf(i)));
		}
		long startTime = System.currentTimeMillis();
		crudOperations.saveAll(objects);
		long endTime = System.currentTimeMillis();
        System.out.println(size+" records, time taken : "+(endTime-startTime)/1000);
		
		List<Map> results = new ArrayList();
		objects.stream().forEach(obj->{
			String id = (String)obj.get(DataUtil.BAN_IDENTIFIER);
			Map result = crudOperations.find(id);
			assertNotNull(result);
		});
	}
	
	private Map createObject(String id) {
		Map<String,String> object = new HashMap();
		object.put(DataUtil.BAN_IDENTIFIER, id);
		object.put("description", "testing echo ban");
		return object;
	}

}
