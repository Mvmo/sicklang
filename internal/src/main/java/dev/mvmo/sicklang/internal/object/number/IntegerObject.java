package dev.mvmo.sicklang.internal.object.number;

import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;

public record IntegerObject(int value) implements SickObject {

    @Override
    public String inspect() {
        return String.format("%d", value);
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.INTEGER;
    }

}
