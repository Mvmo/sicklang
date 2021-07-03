package dev.mvmo.sicklang.internal.builtin.function;

import com.google.common.collect.Lists;
import dev.mvmo.sicklang.internal.object.BuiltinFunctionObject;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.array.ArrayObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;

public class TailFunction extends BuiltinFunctionObject {

    public TailFunction() {
        super("tail", args -> {
            if (args.size() != 1)
                return ErrorObject.newInstance("wrong number of arguments. got=%d, want=%d", args.size(), 1);

            if (!args.get(0).objectType().equals(ObjectType.ARRAY))
                return ErrorObject.newInstance("argument to `tail` must be ARRAY, got %s", args.get(0).objectType());

            var array = (ArrayObject) args.get(0);
            if (array.elements().size() > 0)
                return new ArrayObject(array.elements().subList(1, array.elements().size()));

            return new ArrayObject(Lists.newArrayList());
        });
    }

}
