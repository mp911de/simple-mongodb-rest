package de.paluch.mongo.rest;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: mark Date: 20.08.12 Time: 13:30
 */
@Path("/")
public class SimpleMongoDBRestResource {

    private MongoConnectionFactory mongoConnectionFactory = MongoConnectionFactory.getInstance();

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Wrapped(element = "result")
    public List<String> listDatabases() throws Exception {
        Mongo mongo = mongoConnectionFactory.getMongo();

        try {
            return mongo.getDatabaseNames();
        } finally {
            mongoConnectionFactory.close(mongo);
        }

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Wrapped(element = "result")
    @Path(value = "{databaseName}")
    public List<String> listCollections(@PathParam("databaseName") String databaseName) throws Exception {


        Mongo mongo = mongoConnectionFactory.getMongo();

        try {
            DB db = getDb(databaseName, mongo);
            return new ArrayList(db.getCollectionNames());

        } finally {
            mongoConnectionFactory.close(mongo);
        }
    }


    private DB getDb(String databaseName, Mongo mongo) throws NotFoundException {
        if (!mongo.getDatabaseNames().contains(databaseName)) {
            throw new NotFoundException("Cannot find Database " + databaseName);
        }

        return mongo.getDB(databaseName);
    }

    @PUT
    @Wrapped(element = "result")
    @Path(value = "{databaseName}/{collectionName}")
    public void createCollection(@PathParam("databaseName") String databaseName,
                                 @PathParam("collectionName") String collectionName) throws Exception {


        Mongo mongo = mongoConnectionFactory.getMongo();

        try {
            DB db = getDb(databaseName, mongo);
            db.createCollection(collectionName, new BasicDBObject());
        } finally {
            mongoConnectionFactory.close(mongo);
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Wrapped(element = "result")
    @Path(value = "{databaseName}/{collectionName}")
    public List<String> listCollection(@PathParam("databaseName") String databaseName,
                                       @PathParam("collectionName") String collectionName) throws Exception {


        Mongo mongo = mongoConnectionFactory.getMongo();

        try {
            DBCollection collection = getCollection(databaseName, collectionName, mongo);
            List<String> result = new ArrayList<String>();
            for (DBObject object : collection.find()) {
                result.add(object.toString());
            }

            return result;
        } finally {
            mongoConnectionFactory.close(mongo);
        }
    }

    private DBCollection getCollection(String databaseName, String collectionName, Mongo mongo)
            throws NotFoundException {
        DB db = getDb(databaseName, mongo);

        if (!db.getCollectionNames().contains(collectionName)) {
            throw new NotFoundException("Cannot find collection " + collectionName);
        }
        return db.getCollection(collectionName);
    }

    private void transform(Map map) {
        ObjectId objectId = (ObjectId) map.get("_id");
        if (objectId != null) {
            map.put("_id", objectId.toStringMongod());
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(value = "{databaseName}/{collectionName}/{objectId}")
    public String getDocument(@PathParam("databaseName") String databaseName,
                              @PathParam("collectionName") String collectionName,
                              @PathParam("objectId") String objectId) throws Exception {


        Mongo mongo = mongoConnectionFactory.getMongo();

        try {
            DBCollection collection = getCollection(databaseName, collectionName, mongo);
            BasicDBObject bdb = new BasicDBObject("_id", new ObjectId(objectId));
            DBCursor cursor = collection.find(bdb);
            String result = null;
            if (cursor.hasNext()) {
                result = cursor.next().toString();
            } else {
                throw new NotFoundException("Cannot find object " + objectId);
            }
            cursor.close();

            return result;
        } finally {
            mongoConnectionFactory.close(mongo);
        }
    }


    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(value = "{databaseName}/{collectionName}/")
    public String insertDocument(@PathParam("databaseName") String databaseName,
                                 @PathParam("collectionName") String collectionName,
                                 String body) throws Exception {

        Mongo mongo = mongoConnectionFactory.getMongo();

        try {
            DBCollection collection = getCollection(databaseName, collectionName, mongo);
            String result = null;
            DBObject update = (DBObject) JSON.parse(body);
            collection.insert(update);
            DBCursor cursor = collection.find(update);
            result = cursor.next().toString();

            cursor.close();

            return result;
        } finally {
            mongoConnectionFactory.close(mongo);
        }

    }


    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(value = "{databaseName}/{collectionName}/{objectId}")
    public String updateDocument(@PathParam("databaseName") String databaseName,
                                 @PathParam("collectionName") String collectionName,
                                 @PathParam("objectId") String objectId, String body) throws Exception {

        Mongo mongo = mongoConnectionFactory.getMongo();

        try {
            DBCollection collection = getCollection(databaseName, collectionName, mongo);
            BasicDBObject bdb = new BasicDBObject("_id", new ObjectId(objectId));
            DBCursor cursor = collection.find(bdb);
            String result = null;
            if (cursor.hasNext()) {
                DBObject update = (DBObject) JSON.parse(body);
                DBObject theObject = collection.findOne(bdb);
                theObject.putAll(update);
                collection.update(bdb, theObject);
                cursor.close();
                cursor = collection.find(bdb);
                result = cursor.next().toString();

            } else {
                throw new NotFoundException("Cannot find object " + objectId);
            }
            cursor.close();

            return result;
        } finally {
            mongoConnectionFactory.close(mongo);
        }

    }

}
