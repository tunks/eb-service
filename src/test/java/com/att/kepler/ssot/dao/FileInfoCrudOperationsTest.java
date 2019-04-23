package com.att.kepler.ssot.dao;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.att.kepler.ssot.model.FileInfo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileInfoCrudOperationsTest {
	@Autowired
	@Qualifier("mongoTemplate")
    private MongoTemplate mongoOperations;
	
	private String BAN_COLLECTION  ="ban_test";
	
	private FileInfoCrudOperations fileInfoOperations;
	
	@Before
	public void setup() {
		fileInfoOperations = new FileInfoCrudOperations(mongoOperations);
	}
	
	@Test
	public void testSave() {
		FileInfo info = new FileInfo();
		info.setId(UUID.randomUUID().toString());
		info.setOriginalFileName("echoFile1");
		fileInfoOperations.save(info);
		
		FileInfo result = fileInfoOperations.find(info.getId());
		assertNotNull(result);
	}

}
