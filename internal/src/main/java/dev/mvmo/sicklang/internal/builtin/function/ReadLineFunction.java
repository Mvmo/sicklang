package dev.mvmo.sicklang.internal.builtin.function;

import dev.mvmo.sicklang.internal.object.BuiltinFunctionObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;
import dev.mvmo.sicklang.internal.object.string.StringObject;

import java.util.Scanner;

public class ReadLineFunction extends BuiltinFunctionObject {

    public ReadLineFunction() {
        super("readLine", args -> {
            if (args.size() > 1)
                return ErrorObject.newInstance("wrong number of arguments to 'readLine'");

            if (args.size() == 1) {
                if (!args.get(0).objectType().equals(ObjectType.STRING))
                    return ErrorObject.newInstance("expected string got=%s", args.get(0).objectType());

                System.out.print(args.get(0).inspect());
            }

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            return new StringObject(input);
        });
    }

}
