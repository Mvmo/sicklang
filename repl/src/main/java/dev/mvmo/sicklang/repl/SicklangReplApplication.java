package dev.mvmo.sicklang.repl;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.evaluator.SicklangEvaluator;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.parser.Parser;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;

import java.util.List;
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

            var evaluated = SicklangEvaluator.eval(programNode);
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
