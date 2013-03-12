package com.parthparekh.service.cache;

import org.apache.commons.lang.StringUtils;

/**
 * Enum for types of Caches
 *
 * @author: Parth Parekh
 **/
public enum CacheType {
    MEMCACHED,
    COUCHBASE,
    EHCACHE;

    public String getType() {
        return this.name();
    }

    public static CacheType getByType(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        type = type.trim().toUpperCase();
        for (CacheType cacheType : values()) {
            if (cacheType.getType().equals(type)) {
                return cacheType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}