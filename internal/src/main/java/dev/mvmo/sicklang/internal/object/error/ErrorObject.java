package dev.mvmo.sicklang.internal.object.error;

import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;

public record ErrorObject(String message) implements SickObject {

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.ERROR;
    }

    public static ErrorObject newError(String message, Object... args) {
        return new ErrorObject(String.format(message, args));
    }

}
