package de.paluch.mongo.rest;

import com.mongodb.Mongo;

import java.net.UnknownHostException;

/**
 * User: mark Date: 20.08.12 Time: 19:40
 */
public class LocalMongoConnectionFactory extends MongoConnectionFactory {

    private Mongo mongo;

    public LocalMongoConnectionFactory() {
        setInstance(this);
    }

    @Override
    protected Mongo getMongoImpl() {
        if(mongo == null)
        {
            try {
                mongo = new Mongo();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return mongo;
    }
}
