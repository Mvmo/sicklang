package dev.mvmo.sicklang.internal.object.string;

import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;

public record StringObject(String value) implements SickObject {

    @Override
    public String inspect() {
        return value;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.STRING;
    }

}
