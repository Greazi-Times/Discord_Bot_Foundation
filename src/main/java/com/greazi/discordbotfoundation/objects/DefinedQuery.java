package com.greazi.discordbotfoundation.objects;

public abstract class DefinedQuery<T> {

    private static final boolean ENABLE_CACHING = false;

    private Query<T> cache;

    public Query<T> query() {
        if (!ENABLE_CACHING) return newQuery();
        if (cache == null) cache = newQuery();
        return cache;
    }

    protected abstract Query<T> newQuery();

}