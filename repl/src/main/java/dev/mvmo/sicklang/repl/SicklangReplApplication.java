package dev.mvmo.sicklang.repl;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.evaluator.SicklangEvaluator;
import dev.mvmo.sicklang.internal.env.SickEnvironment;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class SicklangReplApplication {

    private static final String DONKEY_ASCII = """
                 _\\      \s
                 /`b \s
            /####J  \s
             |\\ ||\s""".indent(2);

    private static final String PROMPT = "$ > ";

    public static void main(String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("--file")) {
                if (args.length < 2) {
                    System.out.println("You specified the --file flag but provided no file!");
                    return;
                }

                Path path = Paths.get(args[1]);
                if (!Files.exists(path)) {
                    System.out.println("The provided file doesn't exist");
                    return;
                }

                if (!Files.isRegularFile(path)) {
                    System.out.println("You didn't provide a regular file!");
                    return;
                }

                evaluateFile(path);
                return;
            }
        }


        var scanner = new Scanner(System.in);

        var environment = SickEnvironment.newInstance();
        while (true) {
            System.out.print(PROMPT);
            var scannedLine = scanner.nextLine();

            var lexer = Lexer.newInstance(scannedLine);
            var parser = new Parser(lexer);
            var programNode = parser.parseProgram();

            if (parser.getErrorMessages().size() != 0) {
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

        var parserErrors = parser.getErrorMessages();

        System.out.println(parserErrors.size() > 1 ? "Oh, you got a bunch of errors, get out of here" : "Only one error, that might be okay");
        System.out.println();
        parserErrors.forEach(System.out::println);
    }

    // TODO proper exception handling
    @SneakyThrows
    public static void evaluateFile(Path path) {
        String sourceCode = String.join("", Files.readAllLines(path));

        var lexer = Lexer.newInstance(sourceCode);
        var parser = new Parser(lexer);

        var programNode = parser.parseProgram();
        if (parser.getErrorMessages().size() > 0) {
            showParserErrors(parser);
            return;
        }

        var environment = SickEnvironment.newInstance();
        var evaluated = SicklangEvaluator.eval(programNode, environment);

        System.out.println(evaluated.inspect());
    }

}
