package dev.mvmo.sicklang.internal.object.bool;

import com.google.common.base.Objects;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.hashkey.Hashable;
import dev.mvmo.sicklang.internal.object.hashkey.HashKey;

public record BooleanObject(boolean value) implements SickObject, Hashable {

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

    @Override
    public HashKey hashKey() {
        return new HashKey(objectType(), hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BooleanObject that = (BooleanObject) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
