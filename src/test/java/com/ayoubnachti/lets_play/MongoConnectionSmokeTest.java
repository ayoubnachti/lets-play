package com.ayoubnachti.lets_play;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class MongoConnectionSmokeTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Test
  void connectsAndPerformsRealReadWrite() {
    String collection = "smoke_test";

    Map<String, Object> document = new HashMap<>();
    document.put("check", "connection-works");

    mongoTemplate.insert(document, collection);
    var results = mongoTemplate.findAll(Map.class, collection);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).get("check")).isEqualTo("connection-works");

    mongoTemplate.dropCollection(collection);
  }
}