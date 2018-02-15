package com.vodafone.charging.accountservice.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedMongoTest extends AbstractMongoTest {

    @Test
    public void testCollectionSave(){
        DB db = getMongo().getDB("test-" + UUID.randomUUID());
        db.dropDatabase();
        DBCollection testCol = db.createCollection("testCol", new BasicDBObject());
        WriteResult writeResult = testCol.save(new BasicDBObject("testDoc", new Date()));
        assertThat(writeResult.wasAcknowledged()).isTrue();
    }
}
