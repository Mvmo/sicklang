package dev.mvmo.sicklang.internal.object.bool;

import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;

public record BooleanObject(boolean value) implements SickObject {

    @Override
    public String inspect() {
        return String.format("%b", value);
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.BOOLEAN;
    }
}
