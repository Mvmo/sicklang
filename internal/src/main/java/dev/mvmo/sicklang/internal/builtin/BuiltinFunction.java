package dev.mvmo.sicklang.internal.builtin;

import dev.mvmo.sicklang.internal.object.SickObject;

import java.util.List;

public interface BuiltinFunction {

    SickObject call(List<SickObject> args);

}
