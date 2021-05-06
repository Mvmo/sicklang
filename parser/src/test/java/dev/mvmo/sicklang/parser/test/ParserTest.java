package dev.mvmo.sicklang.parser.test;

import com.google.common.collect.Lists;
import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.parser.Parser;
import dev.mvmo.sicklang.parser.ast.expression.*;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.ExpressionStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.LetStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.ReturnStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.StatementNode;
import lombok.Value;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void test$letStatements() {
        String input = "let x = 5;" +
                "let y = 10;" +
                "let foobar = 838383;";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();
        checkParserErrors(parser);

        assertNotNull(programNode);
        assertEquals(3, programNode.statementNodes().size());

        String[] expectedIdentifiers = new String[]{
                "x",
                "y",
                "foobar"
        };

        for (int i = 0; i < expectedIdentifiers.length; i++) {
            StatementNode statementNode = programNode.statementNodes().get(i);
            testLetStatement(statementNode, expectedIdentifiers[i]);
        }
    }

    @Test
    public void test$returnStatements() {
        String input = "return 5;" +
                "return 10;" +
                "return 993322;";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();
        checkParserErrors(parser);

        assertNotNull(programNode);
        assertEquals(3, programNode.statementNodes().size());

        for (StatementNode statementNode : programNode.statementNodes()) {
            assertTrue(statementNode instanceof ReturnStatementNode);
            assertEquals("return", statementNode.tokenLiteral());
        }
    }

    @Test
    public void test$identifierExpression() {
        String input = "foobar;";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();
        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof IdentifierExpressionNode);

        IdentifierExpressionNode identifierNode = (IdentifierExpressionNode) statementNode.expressionNode();

        assertEquals("foobar", identifierNode.value());
        assertEquals("foobar", identifierNode.tokenLiteral());
    }

    @Test
    public void test$integerLiteralExpression() {
        String input = "5;";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();
        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof IntegerLiteralExpressionNode);

        IntegerLiteralExpressionNode literalExpressionNode = (IntegerLiteralExpressionNode) statementNode.expressionNode();

        assertEquals(5, literalExpressionNode.value());
        assertEquals("5", literalExpressionNode.tokenLiteral());
    }

    @Test
    public void test$prefixExpressions() {
        @Value
        class Expected {
            String input;
            String operator;
            int value;
        }

        List<Expected> expectedList = Lists.newArrayList(
                new Expected("!5", "!", 5),
                new Expected("-15;", "-", 15)
        );

        for (Expected expected : expectedList) {
            Lexer lexer = Lexer.newInstance(expected.input);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            checkParserErrors(parser);

            assertEquals(1, programNode.statementNodes().size());
            assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

            ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

            assertTrue(statementNode.expressionNode() instanceof PrefixExpressionNode);

            PrefixExpressionNode prefixExpressionNode = (PrefixExpressionNode) statementNode.expressionNode();

            assertEquals(expected.operator, prefixExpressionNode.operator());
            testIntegerLiteral(expected.value, prefixExpressionNode.right());
        }
    }

    @Test
    public void test$infixExpressions() {
        @Value
        class Expected {
            String input;
            int leftValue;
            String operator;
            int rightValue;
        }

        List<Expected> expectedList = Lists.newArrayList(
            new Expected("5 + 5;", 5, "+", 5), // +
            new Expected("5 - 5;", 5, "-", 5), // -
            new Expected("5 * 5;", 5, "*", 5), // *
            new Expected("5 / 5;", 5, "/", 5), // /
            new Expected("5 > 5;", 5, ">", 5), // >
            new Expected("5 < 5;", 5, "<", 5), // <
            new Expected("5 == 5;", 5, "==", 5), // ==
            new Expected("5 != 5;", 5, "!=", 5)// !=
        );

        for (Expected expected : expectedList) {
            Lexer lexer = Lexer.newInstance(expected.input);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            checkParserErrors(parser);

            assertEquals(1, programNode.statementNodes().size());
            assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

            ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

            assertTrue(statementNode.expressionNode() instanceof InfixExpressionNode);

            InfixExpressionNode infixExpressionNode = (InfixExpressionNode) statementNode.expressionNode();

            testIntegerLiteral(expected.leftValue, infixExpressionNode.left());

            assertEquals(expected.operator, infixExpressionNode.operator());

            testIntegerLiteral(expected.rightValue, infixExpressionNode.right());
        }
    }

    private void testLetStatement(StatementNode statement, String name) {
        assertEquals("let", statement.tokenLiteral());
        assertTrue("Statement is not instanceof LetStatementNode", statement instanceof LetStatementNode);

        LetStatementNode letStatement = (LetStatementNode) statement;

        assertEquals(name, letStatement.identifier().value());
        assertEquals(name, letStatement.identifier().tokenLiteral());
    }

    private void testIntegerLiteral(int expectedValue, ExpressionNode expressionNode) {
        assertTrue(expressionNode instanceof IntegerLiteralExpressionNode);

        IntegerLiteralExpressionNode literalExpressionNode = (IntegerLiteralExpressionNode) expressionNode;

        assertEquals(expectedValue, literalExpressionNode.value());
        assertEquals(String.valueOf(expectedValue), literalExpressionNode.tokenLiteral());
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
