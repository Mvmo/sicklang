package dev.mvmo.sicklang.internal.env;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SickEnvironment {

    private final Map<String, Object> storageMap;

    public SickEnvironment set(String key, Object value) {
        this.storageMap.put(key, value);
        return this;
    }

    public <T> T setAndGet(String key, T value) {
        set(key, value);
        return value;
    }

    public Object get(String key) {
        return storageMap.get(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        if (hasKey(key))
            return get(key);
        return defaultValue;
    }

    public boolean hasKey(String key) {
        return storageMap.containsKey(key);
    }

    public static SickEnvironment newInstance() {
        return new SickEnvironment(Maps.newHashMap());
    }

}
