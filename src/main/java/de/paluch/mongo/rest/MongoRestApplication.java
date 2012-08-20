package de.paluch.mongo.rest;

import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: mark Date: 20.08.12 Time: 13:37
 */
public class MongoRestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        new LocalMongoConnectionFactory();
        return new HashSet<Class<?>>(Arrays.asList(SimpleMongoDBRestResource.class, NotFoundExceptionMapper.class));
    }
}
