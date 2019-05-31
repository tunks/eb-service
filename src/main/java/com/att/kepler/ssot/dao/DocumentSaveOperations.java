package com.att.kepler.ssot.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

/***
 * Ban Details Crud Operations
 * 
 * @author sj204x
 */

public class DocumentSaveOperations implements SaveOperations<Document> {
	private final static UpdateOptions UPDATE_OPTIONS = new UpdateOptions().upsert(true);
	private static final Logger logger = LoggerFactory.getLogger(DocumentSaveOperations.class);

	private MongoOperations mongoOperations;
	private String collectionName;
	private String identifier;
	private MongoCollection<Document> collection;

	public DocumentSaveOperations(MongoOperations mongoOperations, String collectionName, String identifier) {
		this.mongoOperations = mongoOperations;
		this.collectionName = collectionName;
		this.identifier = identifier;
		this.collection = mongoOperations.getCollection(collectionName);
	}

	@Override
	public void save(Document object) {
		Object value = object.get(identifier);
		Document toInsert = new Document();
		for (Map.Entry<String, Object> entry : object.entrySet()) {
			if (!entry.getKey().equals(identifier)) {
				toInsert.append(entry.getKey(), entry.getValue());
			}
		}
		Document upsert = new Document().append("$setOnInsert", toInsert).append("$set", new Document(identifier, value));

		collection.updateOne(Filters.eq(identifier, value), upsert, UPDATE_OPTIONS);
	}

	@Override
	public void saveAll(List<Document> objects) {
		try {
			if (objects.isEmpty()) {
				logger.info("Empty records");
				return;
			}
			collection.bulkWrite(objects.parallelStream().map(doc -> {
				Object value = doc.get(identifier);
				return new UpdateOneModel<Document>(Filters.eq(identifier, value),new Document("$set", doc),UPDATE_OPTIONS);
			}).collect(Collectors.toList()), new BulkWriteOptions().ordered(false));
		} catch (Exception ex) {
			logger.error("Bulk write failed: " + ex.getMessage());
		}
	}

}
