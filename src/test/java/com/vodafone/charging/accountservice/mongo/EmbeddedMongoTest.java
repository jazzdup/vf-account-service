package com.vodafone.charging.accountservice.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * actually an integration test, will remove from develop branch
 * Ravi Aghera @raviaghera  commented 5 minutes ago
 This is more of an integration test, I wouldn't keep it tbh.
 If you want to keep it then I would suggest having a new test category called "RepositoryTests" which call the Repo and have an actual DB. You would also need to add another test execution in maven to run these (probably after unit tests but before IT).
 */
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
