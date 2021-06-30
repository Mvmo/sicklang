package dev.mvmo.sicklang.internal.object;

import dev.mvmo.sicklang.internal.builtin.BuiltinFunction;

public record BuiltinFunctionObject(BuiltinFunction function) implements SickObject {

    @Override
    public String inspect() {
        return "builtin function";
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.BUILTIN;
    }

}
