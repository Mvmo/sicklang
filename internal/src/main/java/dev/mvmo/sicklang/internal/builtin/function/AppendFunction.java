package dev.mvmo.sicklang.internal.builtin.function;

import dev.mvmo.sicklang.internal.object.BuiltinFunctionObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.array.ArrayObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;

import java.util.ArrayList;

public class AppendFunction extends BuiltinFunctionObject {

    public AppendFunction() {
        super("append", args -> {
            if (args.size() != 2)
                return ErrorObject.formatted("wrong number of arguments. got=%d, want=%d", args.size(), 2);
            if (!args.get(0).objectType().equals(ObjectType.ARRAY))
                return ErrorObject.formatted("first argument to `append` must be ARRAY, got %s", args.get(0).objectType());

            var array = (ArrayObject) args.get(0);

            var newElements = new ArrayList<>(array.elements());
            newElements.add(args.get(1));

            return new ArrayObject(newElements);
        });
    }

}
