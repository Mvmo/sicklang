package dev.mvmo.sicklang.internal.builtin.function;

import dev.mvmo.sicklang.internal.object.BuiltinFunctionObject;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.array.ArrayObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;

public class FirstFunction extends BuiltinFunctionObject {

    public FirstFunction() {
        super("first", args -> {
            if (args.size() != 1)
                return ErrorObject.formatted("wrong number of arguments. got=%d, want=%d", args.size(), 1);

            if (!args.get(0).objectType().equals(ObjectType.ARRAY))
                return ErrorObject.formatted("argument to `first` must be ARRAY, got %s", args.get(0).objectType());

            var array = (ArrayObject) args.get(0);
            if (array.elements().size() > 0)
                return array.elements().get(0);

            return NullObject.NULL;
        });
    }

}
