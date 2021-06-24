package dev.mvmo.sicklang.internal.object;

public record NullObject() implements SickObject {

    public static NullObject NULL = new NullObject();

    @Override
    public String inspect() {
        return "null";
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.NULL;
    }

    public static NullObject get() {
        return NULL;
    }

}
