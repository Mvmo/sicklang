package dev.mvmo.sicklang.internal.object.number;

import com.google.common.base.Objects;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.hashkey.HasHash;
import dev.mvmo.sicklang.internal.object.hashkey.HashKey;

public record IntegerObject(int value) implements SickObject, HasHash {

    @Override
    public String inspect() {
        return String.format("%d", value);
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.INTEGER;
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
        IntegerObject that = (IntegerObject) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
