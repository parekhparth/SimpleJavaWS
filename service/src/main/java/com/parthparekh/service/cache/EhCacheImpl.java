package com.parthparekh.service.cache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memcache cache implementation
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
public class EhCacheImpl implements Cache {
    private static Logger logger = LoggerFactory.getLogger(EhCacheImpl.class);

    private Ehcache cache;

    public Ehcache getCache() {
        return cache;
    }

    public void setCache(Ehcache cache) {
        this.cache = cache;
    }

    @Override
    public <T> boolean put(String key, T value) {
        return put(key, value, 0);
    }

    @Override
    public <T> boolean put(String key, T value, int ttl) {
        Element entry = new Element(key, value);
        if(ttl > 0) {
            entry.setTimeToLive(ttl);
        }
        try {
            cache.put(entry);
        }
        catch (Exception e) {
            logger.warn("unable to update cache: key=" + key + "; value=" + value + "; e=" + e);
        }
        return true;
    }

    @Override
    public boolean evict(String key) {
        boolean success = false;
        try {
            success = cache.remove(key);
        }
        catch (Exception e) {
            logger.warn("unable to evict cache: key=" + key + "; e=" + e);
        }
        return success;
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Element entry = cache.get(key);
        if (entry != null) {
            return (T) entry.getObjectValue();
        }
        return null;
    }

    @Override
    public CacheType getType() {
        return CacheType.EHCACHE;
    }

    public void removeAll() {
        cache.removeAll();
    }
}