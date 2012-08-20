package de.paluch.mongo.rest;

import com.mongodb.Mongo;

/**
 * User: mark Date: 20.08.12 Time: 17:46
 */
public abstract class MongoConnectionFactory {
    private static MongoConnectionFactory instance;

    public static MongoConnectionFactory getInstance() {
        return instance;
    }

    public static void setInstance(MongoConnectionFactory instance) {
        MongoConnectionFactory.instance = instance;
    }

    Mongo getMongo()
    {
        return instance.getMongoImpl();
    }

    protected abstract Mongo getMongoImpl();

    void close(Mongo mongo)
    {
       // mongo.close();
    }
}
