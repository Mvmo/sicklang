package dev.mvmo.sicklang.internal.env;

import com.google.common.collect.Maps;

import java.util.Map;

public record SickEnvironment(Map<String, Object> storage) {

    public static SickEnvironment newInstance() {
        return new SickEnvironment(Maps.newHashMap());
    }

}
