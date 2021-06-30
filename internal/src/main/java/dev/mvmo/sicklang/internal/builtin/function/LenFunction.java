package dev.mvmo.sicklang.internal.builtin.function;

import dev.mvmo.sicklang.internal.object.BuiltinFunctionObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.internal.object.string.StringObject;

public class LenFunction extends BuiltinFunctionObject {

    public LenFunction() {
        super("len", args -> {
            if (args.size() != 1)
                return ErrorObject.newInstance("wrong number of arguments. got=%d, want=%d", args.size(), 1);

            var arg = args.get(0);
            if (arg instanceof StringObject) {
                return new IntegerObject(((StringObject) arg).value().length());
            }

            return ErrorObject.newInstance("argument to `len` not supported. got %s", arg.objectType());
        });
    }

}
