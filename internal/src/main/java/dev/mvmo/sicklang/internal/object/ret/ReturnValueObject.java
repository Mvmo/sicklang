package dev.mvmo.sicklang.internal.object.ret;

import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;

public record ReturnValueObject(SickObject value) implements SickObject {

    @Override
    public String inspect() {
        return value.inspect();
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.RETURN_VALUE;
    }
}
