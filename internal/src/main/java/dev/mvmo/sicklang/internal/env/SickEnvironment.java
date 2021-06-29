package dev.mvmo.sicklang.internal.env;

import com.google.common.collect.Maps;
import dev.mvmo.sicklang.internal.object.SickObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SickEnvironment {

    private final Map<String, SickObject> storageMap;
    private final SickEnvironment parent;

    public SickEnvironment set(String key, SickObject value) {
        this.storageMap.put(key, value);
        return this;
    }

    public SickObject setAndGet(String key, SickObject value) {
        set(key, value);
        return value;
    }

    public SickObject get(String key) {
        return storageMap.get(key);
    }

    public SickObject getOrDefault(String key, SickObject defaultValue) {
        if (hasKey(key))
            return get(key);
        return defaultValue;
    }

    public boolean hasKey(String key) {
        return storageMap.containsKey(key);
    }

    public static SickEnvironment newEnclosedInstance(SickEnvironment environment) {
        return new SickEnvironment(Maps.newHashMap(), environment);
    }

    public static SickEnvironment newInstance() {
        return new SickEnvironment(Maps.newHashMap(), null);
    }

}
