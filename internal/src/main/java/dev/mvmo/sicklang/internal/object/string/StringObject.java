package dev.mvmo.sicklang.internal.object.string;

import com.google.common.base.Objects;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.hashkey.Hashable;
import dev.mvmo.sicklang.internal.object.hashkey.HashKey;

public record StringObject(String value) implements SickObject, Hashable {

    @Override
    public String inspect() {
        return value;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.STRING;
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
        StringObject that = (StringObject) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
