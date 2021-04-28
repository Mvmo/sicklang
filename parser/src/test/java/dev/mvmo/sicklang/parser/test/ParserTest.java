package dev.mvmo.sicklang.parser.test;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.parser.Parser;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.LetStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.StatementNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void test$letStatements() {
        String input = "let x = 5;" +
                "let y = 10;" +
                "let foobar = 838383;";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode node = parser.parseProgram();
        checkParserErrors(parser);

        assertNotNull(node);
        assertEquals(3, node.statementNodes().size());

        String[] expectedIdentifiers = new String[]{
                "x",
                "y",
                "foobar"
        };

        for (int i = 0; i < expectedIdentifiers.length; i++) {
            StatementNode statementNode = node.statementNodes().get(i);
            testLetStatement(statementNode, expectedIdentifiers[i]);
        }
    }

    private void testLetStatement(StatementNode statement, String name) {
        assertEquals("let", statement.tokenLiteral());
        assertTrue("Statement is not instanceof LetStatementNode", statement instanceof LetStatementNode);

        LetStatementNode letStatement = (LetStatementNode) statement;

        assertEquals(name, letStatement.identifier().value());
        assertEquals(name, letStatement.identifier().tokenLiteral());
    }

    private void checkParserErrors(Parser parser) {
        if (parser.errorMessages().size() == 0)
            return;

        System.err.printf("parser has %d errors\n", parser.errorMessages().size());
        parser.errorMessages().stream()
                .map(message -> "parser error: " + message)
                .forEach(System.err::println);

        fail();
    }

}
