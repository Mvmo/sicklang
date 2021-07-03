package dev.mvmo.sicklang.internal.builtin.function;

import dev.mvmo.sicklang.internal.object.BuiltinFunctionObject;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.array.ArrayObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;

public class LastFunction extends BuiltinFunctionObject {

    public LastFunction() {
        super("last", args -> {
            if (args.size() != 1)
                return ErrorObject.newInstance("wrong number of arguments. got=%d, want=%d", args.size(), 1);

            if (!args.get(0).objectType().equals(ObjectType.ARRAY))
                return ErrorObject.newInstance("argument to `last` must be ARRAY, got %s", args.get(0).objectType());

            var array = (ArrayObject) args.get(0);
            var length = array.elements().size();

            if (length > 0)
                return array.elements().get(length - 1);

            return NullObject.NULL;
        });
    }

}
