package dev.mvmo.sicklang.internal.object.bool;

import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;

public record BooleanObject(boolean value) implements SickObject {

    public static final BooleanObject TRUE = new BooleanObject(true);
    public static final BooleanObject FALSE = new BooleanObject(false);

    @Override
    public String inspect() {
        return String.format("%b", value);
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.BOOLEAN;
    }

    public static BooleanObject fromNative(boolean bool) {
        return bool ? TRUE : FALSE;
    }

}
