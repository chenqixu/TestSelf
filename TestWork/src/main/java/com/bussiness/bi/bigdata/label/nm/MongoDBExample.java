package com.bussiness.bi.bigdata.label.nm;

import com.mongodb.ExplainVerbosity;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Projections.include;

/**
 * TODO
 *
 * @author chenqixu
 */
public class MongoDBExample {
    public static void main(String[] args) {
        MongoDatabase database = MongoClients.create().getDatabase("yourDatabase");
        MongoCollection<Document> collection = database.getCollection("yourCollection");

        // 构建查询条件
        Document query = new Document("status", "A");

        // 执行查询并获取执行计划
        Document explain = collection.find(query).explain(ExplainVerbosity.QUERY_PLANNER);
        System.out.println(explain.toJson()); // 打印执行计划，其中包括AST信息
    }
}
