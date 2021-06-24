package dev.mvmo.sicklang.internal.object;

public record NullObject() implements SickObject {

    @Override
    public String inspect() {
        return "null";
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.NULL;
    }
}
