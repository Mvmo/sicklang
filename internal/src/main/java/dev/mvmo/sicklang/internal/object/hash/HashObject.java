package dev.mvmo.sicklang.internal.object.hash;

import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.hashkey.HashKey;

import java.util.Map;
import java.util.stream.Collectors;

public record HashObject(Map<HashKey, Entry> pairs) implements SickObject {

    @Override
    public String inspect() {
        return String.format("{%s}", pairs.values().stream()
                .map(entry -> String.format("%s: %s", entry.key().inspect(), entry.value().inspect()))
                .collect(Collectors.joining(", ")));
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.HASH;
    }

    public static record Entry(SickObject key, SickObject value) {
    }

}
