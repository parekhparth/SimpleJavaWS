package com.parthparekh.service.cache;

/**
 * Cache API with basic cache operations
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 */
public interface Cache {

    /**
     * Adds data to the cache
     *
     * @param key - cache key
     * @param value - value of object
     * @return true if the operation is successful, false otherwise
     */
    public <T> boolean put(String key, T value);

    /**
     * Adds data to the cache with specific ttl
     *
     * @param key - cache key
     * @param value - value of object
     * @param ttl in seconds
     * @return true if the operation is successful, false otherwise
     */
    public <T> boolean put(String key, T value, int ttl);

    /**
     * Removes data from cache with specified key
     *
     * @param key - cache key
     * @return true if the operation is successful, false otherwise
     */
    public boolean evict(String key);

    /**
     * Retrieves data from cache with specified key
     *
     * @param key - cache key
     * @param clazz - class type of object
     * @return value of T object, null otherwise
     */
	public <T> T get(String key, Class<T> clazz);

    /**
     * Returns the CacheType for the cache
     *
     * @return the CacheType of the cache
     */
	public CacheType getType();
}
