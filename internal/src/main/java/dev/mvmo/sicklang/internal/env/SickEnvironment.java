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
        var val = storageMap.get(key);
        if (val == null && parent != null)
            return parent.get(key);

        return val;
    }

    public SickObject getOrDefault(String key, SickObject defaultValue) {
        if (hasKey(key))
            return get(key);
        return defaultValue;
    }

    public boolean hasKey(String key) {
        boolean contains = storageMap.containsKey(key);
        if (!contains && parent != null)
            return parent.hasKey(key);

        return contains;
    }

    public static SickEnvironment newEnclosedInstance(SickEnvironment environment) {
        return new SickEnvironment(Maps.newHashMap(), environment);
    }

    public static SickEnvironment newInstance() {
        return new SickEnvironment(Maps.newHashMap(), null);
    }

}
