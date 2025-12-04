package com.ly.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for rule evaluation and action execution.
 * Holds the data that rules operate on.
 */
public class RuleContext {
    private final Map<String, Object> data = new HashMap<>();

    public RuleContext() {};

    public RuleContext(Map<String, Object> initialData) {
        if (initialData != null) {
            this.data.putAll(initialData);
        }
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = data.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }

    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }
}

