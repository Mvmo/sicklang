package dev.mvmo.sicklang.parser.test;

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
    public void test$booleanExpression() {
        String input = "true;";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();
        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof BooleanExpressionNode);

        BooleanExpressionNode booleanExpressionNode = (BooleanExpressionNode) statementNode.expressionNode();

        assertTrue(booleanExpressionNode.value());
        assertEquals("true", booleanExpressionNode.tokenLiteral());
    }

    @Test
    public void test$prefixExpressions() {
        @Value
        class TestCase<T> {
            String input;
            String operator;
            T value;
        }

        TestCase<?>[] testCases = new TestCase[]{
                new TestCase<>("!5", "!", 5),
                new TestCase<>("-15;", "-", 15),
                new TestCase<>("!true", "!", true),
                new TestCase<>("!false", "!", false)
        };

        for (TestCase<?> testCase : testCases) {
            Lexer lexer = Lexer.newInstance(testCase.input);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            checkParserErrors(parser);

            assertEquals(1, programNode.statementNodes().size());
            assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

            ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

            assertTrue(statementNode.expressionNode() instanceof PrefixExpressionNode);

            PrefixExpressionNode prefixExpressionNode = (PrefixExpressionNode) statementNode.expressionNode();

            assertEquals(testCase.operator, prefixExpressionNode.operator());
            testLiteralExpression(testCase.value, prefixExpressionNode.right());
        }
    }

    @Test
    public void test$infixExpressions() {
        @Value
        class TestCase<T> {
            String input;
            T leftValue;
            String operator;
            T rightValue;
        }

        TestCase<?>[] testCases = new TestCase[]{
                new TestCase<>("5 + 5;", 5, "+", 5), // +
                new TestCase<>("5 - 5;", 5, "-", 5), // -
                new TestCase<>("5 * 5;", 5, "*", 5), // *
                new TestCase<>("5 / 5;", 5, "/", 5), // /
                new TestCase<>("5 > 5;", 5, ">", 5), // >
                new TestCase<>("5 < 5;", 5, "<", 5), // <
                new TestCase<>("5 == 5;", 5, "==", 5), // ==
                new TestCase<>("5 != 5;", 5, "!=", 5), // !=
                new TestCase<>("true == true;", true, "==", true),
                new TestCase<>("true != false;", true, "!=", false),
                new TestCase<>("false == false", false, "==", false)
        };

        for (TestCase<?> testCase : testCases) {
            Lexer lexer = Lexer.newInstance(testCase.input);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            checkParserErrors(parser);

            assertEquals(1, programNode.statementNodes().size());
            assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

            ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

            testInfixExpression(testCase.leftValue, testCase.operator, testCase.rightValue, statementNode.expressionNode());
        }
    }

    @Test
    public void test$operatorPrecedence() {
        @Value
        class TestCase {
            String input;
            String expected;
        }

        TestCase[] testCases = new TestCase[]{
                new TestCase("-a * b", "((-a) * b)"),
                new TestCase("!-a", "(!(-a))"),
                new TestCase("a + b + c", "((a + b) + c)"),
                new TestCase("a + b - c", "((a + b) - c)"),
                new TestCase("a * b * c", "((a * b) * c)"),
                new TestCase("a * b / c", "((a * b) / c)"),
                new TestCase("a + b / c", "(a + (b / c))"),
                new TestCase("a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)"),
                new TestCase("3 + 4; -5 * 5", "(3 + 4)((-5) * 5)"),
                new TestCase("5 > 4 == 3 < 4", "((5 > 4) == (3 < 4))"),
                new TestCase("5 < 4 != 3 > 4", "((5 < 4) != (3 > 4))"),
                new TestCase("3 + 4 * 5 == 3  * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"),
                new TestCase("true", "true"),
                new TestCase("false", "false"),
                new TestCase("3 > 5 == false", "((3 > 5) == false)"),
                new TestCase("3 < 5 == true", "((3 < 5) == true)"),
                new TestCase("1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"),
                new TestCase("(5 + 5) * 2", "((5 + 5) * 2)"),
                new TestCase("2 / (5 + 5)", "(2 / (5 + 5))"),
                new TestCase("-(5 + 5)", "(-(5 + 5))"),
                new TestCase("!(true == true)", "(!(true == true))"),
        };

        for (TestCase testCase : testCases) {
            Lexer lexer = Lexer.newInstance(testCase.input);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            checkParserErrors(parser);

            assertEquals(testCase.expected, programNode.toString());
        }
    }

    @Test
    public void test$ifExpression() {
        String input = "if (x < y) { x }";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();
        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof IfExpressionNode);

        IfExpressionNode ifExpressionNode = (IfExpressionNode) statementNode.expressionNode();

        testInfixExpression("x", "<", "y", ifExpressionNode.conditionalExpressionNode());

        assertEquals(1, ifExpressionNode.consequence().statementNodes().size());
        assertTrue(ifExpressionNode.consequence().statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode consequenceStatementNode = (ExpressionStatementNode) ifExpressionNode.consequence().statementNodes().get(0);

        testIdentifier("x", consequenceStatementNode.expressionNode());

        assertNull(ifExpressionNode.alternative());
    }

    @Test
    public void test$ifElseExpression() {
        String input = "if (x < y) { x } else { y }";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();
        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof IfExpressionNode);

        IfExpressionNode ifExpressionNode = (IfExpressionNode) statementNode.expressionNode();

        testInfixExpression("x", "<", "y", ifExpressionNode.conditionalExpressionNode());

        assertEquals(1, ifExpressionNode.consequence().statementNodes().size());
        assertTrue(ifExpressionNode.consequence().statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode consequenceStatementNode = (ExpressionStatementNode) ifExpressionNode.consequence().statementNodes().get(0);

        testIdentifier("x", consequenceStatementNode.expressionNode());

        assertEquals(1, ifExpressionNode.alternative().statementNodes().size());
        assertTrue(ifExpressionNode.alternative().statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode alternativeStatementNode = (ExpressionStatementNode) ifExpressionNode.alternative().statementNodes().get(0);

        testIdentifier("y", alternativeStatementNode.expressionNode());
    }

    @Test
    public void test$functionLiteral() {
        String input = "fn(x, y) { x + y; }";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();
        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof FunctionLiteralExpressionNode);

        FunctionLiteralExpressionNode functionLiteralExpressionNode = (FunctionLiteralExpressionNode) statementNode.expressionNode();

        assertEquals(2, functionLiteralExpressionNode.parameters().size());

        testLiteralExpression("x", functionLiteralExpressionNode.parameters().get(0));
        testLiteralExpression("y", functionLiteralExpressionNode.parameters().get(1));

        assertEquals(1, functionLiteralExpressionNode.body().statementNodes().size());
        assertTrue(functionLiteralExpressionNode.body().statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode bodyExpressionNode = (ExpressionStatementNode) functionLiteralExpressionNode.body().statementNodes().get(0);

        testInfixExpression("x", "+", "y", bodyExpressionNode.expressionNode());
    }

    @Test
    public void test$functionParameters() {
        @Value
        class TestCase {
            String input;
            String[] expectedParamIdentifiers;
        }

        TestCase[] testCases = new TestCase[]{
                new TestCase("fn() {};", new String[0]),
                new TestCase("fn(x) {};", new String[]{"x"}),
                new TestCase("fn(x, y, z) {};", new String[]{"x", "y", "z"})
        };

        for (TestCase testCase : testCases) {
            Lexer lexer = Lexer.newInstance(testCase.input);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            checkParserErrors(parser);

            assertTrue(programNode.statementNodes().size() >= 1);
            assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

            ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

            assertTrue(statementNode.expressionNode() instanceof FunctionLiteralExpressionNode);

            FunctionLiteralExpressionNode functionLiteralExpressionNode = (FunctionLiteralExpressionNode) statementNode.expressionNode();

            assertEquals(testCase.expectedParamIdentifiers.length, functionLiteralExpressionNode.parameters().size());

            for (int i = 0; i < testCase.expectedParamIdentifiers.length; i++)
                testLiteralExpression(testCase.expectedParamIdentifiers[i], functionLiteralExpressionNode.parameters().get(i));
        }
    }

    private void testLetStatement(StatementNode statement, String name) {
        assertEquals("let", statement.tokenLiteral());
        assertTrue("Statement is not instanceof LetStatementNode", statement instanceof LetStatementNode);

        LetStatementNode letStatement = (LetStatementNode) statement;

        assertEquals(name, letStatement.identifier().value());
        assertEquals(name, letStatement.identifier().tokenLiteral());
    }

    private IntegerLiteralExpressionNode testIntegerLiteral(int expectedValue, ExpressionNode expressionNode) {
        assertTrue(expressionNode instanceof IntegerLiteralExpressionNode);

        IntegerLiteralExpressionNode literalExpressionNode = (IntegerLiteralExpressionNode) expressionNode;

        assertEquals(expectedValue, literalExpressionNode.value());
        assertEquals(String.valueOf(expectedValue), literalExpressionNode.tokenLiteral());

        return literalExpressionNode;
    }

    private InfixExpressionNode testInfixExpression(Object expectedLeft, String expectedOperator, Object expectedRight, ExpressionNode expressionNode) {
        assertTrue(expressionNode instanceof InfixExpressionNode);

        InfixExpressionNode infixExpressionNode = (InfixExpressionNode) expressionNode;

        testLiteralExpression(expectedLeft, infixExpressionNode.left());
        assertEquals(expectedOperator, infixExpressionNode.operator());
        testLiteralExpression(expectedRight, infixExpressionNode.right());

        return infixExpressionNode;
    }

    private BooleanExpressionNode testBooleanLiteral(boolean expectedValue, ExpressionNode expressionNode) {
        assertTrue(expressionNode instanceof BooleanExpressionNode);

        BooleanExpressionNode booleanExpressionNode = (BooleanExpressionNode) expressionNode;

        assertEquals(expectedValue, booleanExpressionNode.value());
        assertEquals(String.valueOf(expectedValue), booleanExpressionNode.tokenLiteral());

        return booleanExpressionNode;
    }

    private IdentifierExpressionNode testIdentifier(String expectedValue, ExpressionNode expressionNode) {
        assertTrue(expressionNode instanceof IdentifierExpressionNode);
        IdentifierExpressionNode identifierExpressionNode = (IdentifierExpressionNode) expressionNode;
        assertEquals(expectedValue, identifierExpressionNode.value());
        assertEquals(expectedValue, identifierExpressionNode.tokenLiteral());

        return identifierExpressionNode;
    }

    private ExpressionNode testLiteralExpression(Object expectedValue, ExpressionNode expressionNode) {
        return switch (expectedValue.getClass().getSimpleName()) {
            case "Integer" -> testIntegerLiteral((Integer) expectedValue, expressionNode);
            case "String" -> testIdentifier((String) expectedValue, expressionNode);
            case "Boolean" -> testBooleanLiteral((Boolean) expectedValue, expressionNode);
            default -> () -> {
                fail();
                return null;
            };
        };
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
