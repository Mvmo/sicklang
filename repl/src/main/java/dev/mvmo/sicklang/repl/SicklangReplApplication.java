package dev.mvmo.sicklang.repl;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.parser.Parser;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.token.Token;
import dev.mvmo.sicklang.token.TokenType;

import java.util.Scanner;

public class SicklangReplApplication {

    private static final String PROMPT = "$ > ";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(PROMPT);
            String scannedLine = scanner.nextLine();

            Lexer lexer = Lexer.newInstance(scannedLine);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();

            while (true) {
                Token token = lexer.nextToken();
                if (token.type().equals(TokenType.EOF)) {
                    System.out.println(">> EOF <<");
                    break;
                }

                System.out.println(token);
            }

            System.out.println("Parser: ");
            System.out.println(programNode.toString());
        }
    }

}
