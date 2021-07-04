package dev.mvmo.sicklang.internal.builtin.function;

import dev.mvmo.sicklang.internal.object.BuiltinFunctionObject;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.SickObject;

public class PrintlnFunction extends BuiltinFunctionObject {

    public PrintlnFunction() {
        super("println", args -> {
            args.stream()
                    .map(SickObject::inspect)
                    .forEach(System.out::println);

            return NullObject.NULL;
        });
    }

}
