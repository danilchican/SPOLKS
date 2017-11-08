package com.bsuir.danilchican.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class Cache {
    private HashMap<Byte, byte[]> cache;
    private final static int CACHE_SIZE = 70;

    /**
     * Logger to getCommand logs.
     */
    static final Logger LOGGER = LogManager.getLogger();

    public Cache() {
        cache = new HashMap<>();
    }

    /**
     * Add new item to cache.
     *
     * @param cacheItem
     * @return boolean
     */
    public void add(byte index, byte[] cacheItem) {
        if(!isFull()) {
            cache.put(index, cacheItem);
            LOGGER.log(Level.DEBUG, "Cache item[" + index + "]");
        }
    }

    public HashMap<Byte, byte[]> get() {
        return cache;
    }

    /**
     * Check if cache is filled to top.
     *
     * @return boolean
     */
    public boolean isFull() {
        return cache.size() == CACHE_SIZE;
    }

    public void clear() {
        cache.clear();
    }
}
