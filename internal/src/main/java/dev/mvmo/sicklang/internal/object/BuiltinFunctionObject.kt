package dev.mvmo.sicklang.internal.object;

import dev.mvmo.sicklang.internal.builtin.BuiltinFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BuiltinFunctionObject implements SickObject {

    private final String name;
    private final BuiltinFunction function;

    @Override
    public String inspect() {
        return name;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.BUILTIN;
    }

}
