package dev.mvmo.sicklang.repl;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.evaluator.SicklangEvaluator;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.parser.Parser;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.token.Token;
import dev.mvmo.sicklang.token.TokenType;

import java.util.List;
import java.util.Scanner;

public class SicklangReplApplication {

    private static final String DONKEY_ASCII = "" +
            "       _\\       \n" +
            "       /`b  \n" +
            "  /####J   \n" +
            "   |\\ || ";

    private static final String PROMPT = "$ > ";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean printLexer = args.length >= 2 && args[1].equals("--lexer");

        while (true) {
            System.out.print(PROMPT);
            String scannedLine = scanner.nextLine();

            Lexer lexer = Lexer.newInstance(scannedLine);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            if (parser.errorMessages().size() != 0) {
                showParserErrors(parser);
                return;
            }

            SickObject evaluated = SicklangEvaluator.eval(programNode);
            if (evaluated == null)
                return;

            System.out.println(evaluated.inspect());
        }
    }

    private static void showParserErrors(Parser parser) {
        System.out.println(DONKEY_ASCII);
        System.out.println("Oh bro, you got an error! Let's see");
        System.out.println();

        List<String> parserErrors = parser.errorMessages();

        System.out.println(parserErrors.size() > 1 ? "Oh, you got a bunch of errors, get out of here" : "Only one error, that might be okay");
        System.out.println();
        parserErrors.forEach(System.out::println);
    }

}
