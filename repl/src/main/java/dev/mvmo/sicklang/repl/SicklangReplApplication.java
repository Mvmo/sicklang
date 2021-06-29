package dev.mvmo.sicklang.repl;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.evaluator.SicklangEvaluator;
import dev.mvmo.sicklang.internal.env.SickEnvironment;
import dev.mvmo.sicklang.parser.Parser;

import java.util.Scanner;

public class SicklangReplApplication {

    private static final String DONKEY_ASCII = """
                 _\\      \s
                 /`b \s
            /####J  \s
             |\\ ||\s""".indent(2);

    private static final String PROMPT = "$ > ";

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        var environment = SickEnvironment.newInstance();
        while (true) {
            System.out.print(PROMPT);
            var scannedLine = scanner.nextLine();

            var lexer = Lexer.newInstance(scannedLine);
            var parser = Parser.newInstance(lexer);
            var programNode = parser.parseProgram();

            if (parser.errorMessages().size() != 0) {
                showParserErrors(parser);
                return;
            }

            var evaluated = SicklangEvaluator.eval(programNode, environment);
            if (evaluated == null)
                return;

            System.out.println(evaluated.inspect());
        }
    }

    private static void showParserErrors(Parser parser) {
        System.out.println(DONKEY_ASCII);
        System.out.println("Oh bro, you got an error! Let's see");
        System.out.println();

        var parserErrors = parser.errorMessages();

        System.out.println(parserErrors.size() > 1 ? "Oh, you got a bunch of errors, get out of here" : "Only one error, that might be okay");
        System.out.println();
        parserErrors.forEach(System.out::println);
    }

}
