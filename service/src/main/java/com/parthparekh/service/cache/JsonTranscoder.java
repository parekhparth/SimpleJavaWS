package com.parthparekh.service.cache;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.SerializingTranscoder;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * JSON transcoder to serialize/deserialize object for caching
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
public class JsonTranscoder extends SerializingTranscoder {
    private ObjectMapper objectMapper;
    private Class<?> entityClass;

    public JsonTranscoder(Class<?> entityClass, ObjectMapper mapper) {
        super(CachedData.MAX_SIZE);

        this.objectMapper = mapper;
        this.entityClass = entityClass;
    }

    @Override
    protected byte[] serialize(Object value) {
		if(value == null) {
			throw new NullPointerException("null cannot be serialized");
		}
		byte[] rv=null;
		try {
			ByteArrayOutputStream ostream =new ByteArrayOutputStream();
			objectMapper.writeValue(ostream, value);
			ostream.close();
			rv=ostream.toByteArray();
		} catch(IOException e) {
			throw new IllegalArgumentException("cannot serialize object: ", e);
		}
		return rv;
    }

    @Override
    protected Object deserialize(byte[] rawData) {
        if(rawData==null || rawData.length==0) {
            return null;
        }
		try {
			ByteArrayInputStream istream =new ByteArrayInputStream(rawData);
			Object value = objectMapper.readValue(rawData, 0, rawData.length, entityClass);
			istream.close();
			return value;
		} catch(IOException e) {
			throw new IllegalArgumentException("cannot deserialize object: ", e);
		}
    }
}