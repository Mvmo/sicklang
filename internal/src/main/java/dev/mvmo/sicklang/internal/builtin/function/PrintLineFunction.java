package dev.mvmo.sicklang.internal.builtin.function;

import dev.mvmo.sicklang.internal.object.BuiltinFunctionObject;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;

import java.util.stream.Collectors;

public class PrintLineFunction extends BuiltinFunctionObject {

    public PrintLineFunction() {
        super("println", args -> {
            if (args.size() == 0)
                return ErrorObject.newInstance("wrong number of arguments to 'print'");

            System.out.println(args.stream()
                    .map(SickObject::inspect)
                    .collect(Collectors.joining(", ")));

            return NullObject.NULL;
        });
    }

}