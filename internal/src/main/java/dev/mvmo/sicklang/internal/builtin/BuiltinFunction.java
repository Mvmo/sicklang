package dev.mvmo.sicklang.internal.builtin;

import dev.mvmo.sicklang.internal.object.SickObject;

public interface BuiltinFunction {

    SickObject call(SickObject... args);

}