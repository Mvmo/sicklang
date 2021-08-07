package dev.mvmo.sicklang.internal.object.array;

import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;

import java.util.List;
import java.util.stream.Collectors;

public record ArrayObject(List<SickObject> elements) implements SickObject {

    @Override
    public String inspect() {
        return String.format("[%s]", elements.stream()
                .map(SickObject::inspect)
                .collect(Collectors.joining(", ")));
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.ARRAY;
    }

}
