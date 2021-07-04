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

import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void test$letStatements() {
        @Value
        class TestCase {
            String input;
            String expectedIdentifier;
            Object expectedValue;
        }

        TestCase[] testCases = new TestCase[]{
                new TestCase("let x = 5;", "x", 5),
                new TestCase("let y = true;", "y", true),
                new TestCase("let foobar = y;", "foobar", "y"),
        };

        for (TestCase testCase : testCases) {
            Lexer lexer = Lexer.newInstance(testCase.input);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            checkParserErrors(parser);

            assertNotNull(programNode);
            assertEquals(1, programNode.statementNodes().size());


            var statementNode = programNode.statementNodes().get(0);

            testLetStatement(statementNode, testCase.expectedIdentifier);

            LetStatementNode letStatementNode = (LetStatementNode) statementNode;

            System.out.println(letStatementNode.value());
            testLiteralExpression(testCase.expectedValue, letStatementNode.value());
        }
    }

    @Test
    public void test$returnStatements() {
        @Value
        class TestCase {
            String input;
            Object expectedValue;
        }

        TestCase[] testCases = new TestCase[]{
                new TestCase("return 5;", 5),
                new TestCase("return 10;", 10),
                new TestCase("return true;", true),
                new TestCase("return x;", "x"),
                new TestCase("return 20;", 20)
        };

        for (TestCase testCase : testCases) {
            Lexer lexer = Lexer.newInstance(testCase.input);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            checkParserErrors(parser);

            assertNotNull(programNode);
            assertEquals(1, programNode.statementNodes().size());

            var statementNode = programNode.statementNodes().get(0);

            assertTrue(statementNode instanceof ReturnStatementNode);

            ReturnStatementNode returnStatementNode = (ReturnStatementNode) statementNode;

            testLiteralExpression(testCase.expectedValue, returnStatementNode.returnValue());
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
                new TestCase("a + add(b * c) + d", "((a + add((b * c))) + d)"),
                new TestCase("add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))", "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))"),
                new TestCase("add(a + b + c * d / f + g)", "add((((a + b) + ((c * d) / f)) + g))"),
                new TestCase("a * [1, 2, 3, 4][b * c] * d", "((a * ([1, 2, 3, 4][(b * c)])) * d)"),
                new TestCase("add(a * b[2], b[1], 2 * [1, 2][1])", "add((a * (b[2])), (b[1]), (2 * ([1, 2][1])))")
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

    @Test
    public void test$callExpression() {
        String input = "add(1, 2 * 3, 4 + 5);";

        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();
        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof CallExpressionNode);

        CallExpressionNode callExpressionNode = (CallExpressionNode) statementNode.expressionNode();

        testIdentifier("add", callExpressionNode.function());

        assertEquals(3, callExpressionNode.arguments().size());

        testLiteralExpression(1, callExpressionNode.arguments().get(0));
        testInfixExpression(2, "*", 3, callExpressionNode.arguments().get(1));
        testInfixExpression(4, "+", 5, callExpressionNode.arguments().get(2));
    }

    @Test
    public void test$callExpressionParameters() {
        @Value
        class TestCase {
            String input;
            String[] expectedParamIdentifiers;
        }

        TestCase[] testCases = new TestCase[]{
                new TestCase("add();", new String[0]),
                new TestCase("add(x);", new String[]{"x"}),
                new TestCase("add(x, y, z);", new String[]{"x", "y", "z"})
        };

        for (TestCase testCase : testCases) {
            Lexer lexer = Lexer.newInstance(testCase.input);
            Parser parser = Parser.newInstance(lexer);

            ProgramNode programNode = parser.parseProgram();
            checkParserErrors(parser);

            assertTrue(programNode.statementNodes().size() >= 1);
            assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

            ExpressionStatementNode statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

            assertTrue(statementNode.expressionNode() instanceof CallExpressionNode);

            CallExpressionNode callExpressionNode = (CallExpressionNode) statementNode.expressionNode();

            assertEquals(testCase.expectedParamIdentifiers.length, callExpressionNode.arguments().size());

            for (int i = 0; i < testCase.expectedParamIdentifiers.length; i++)
                testLiteralExpression(testCase.expectedParamIdentifiers[i], callExpressionNode.arguments().get(i));
        }
    }

    @Test
    public void test$stringLiteralExpression() {
        var input = "\"hello world\"";

        var lexer = Lexer.newInstance(input);
        var parser = Parser.newInstance(lexer);
        var programNode = parser.parseProgram();

        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        var statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof StringLiteralExpressionNode);

        var stringExpression = (StringLiteralExpressionNode) statementNode.expressionNode();

        assertEquals("hello world", stringExpression.tokenLiteral());
    }

    @Test
    public void test$arrayLiteralExpressions() {
        var input = "[1, 2 * 2, 3 + 3]";

        var lexer = Lexer.newInstance(input);
        var parser = Parser.newInstance(lexer);
        var programNode = parser.parseProgram();

        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        var statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof ArrayLiteralExpressionNode);

        var arrayExpression = (ArrayLiteralExpressionNode) statementNode.expressionNode();

        assertEquals(3, arrayExpression.elements().size());

        testIntegerLiteral(1, arrayExpression.elements().get(0));
        testInfixExpression(2, "*", 2, arrayExpression.elements().get(1));
        testInfixExpression(3, "+", 3, arrayExpression.elements().get(2));
    }

    @Test
    public void test$indexExpression() {
        var input = "myArray[1 + 1]";

        var lexer = Lexer.newInstance(input);
        var parser = Parser.newInstance(lexer);
        var programNode = parser.parseProgram();

        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        var statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof IndexExpressionNode);

        var indexExpression = (IndexExpressionNode) statementNode.expressionNode();

        testIdentifier("myArray", indexExpression.left());
        testInfixExpression(1, "+", 1, indexExpression.index());
    }

    @Test
    public void test$hashLiteralStringKeys() {
        String input = "{\"one\": 1, \"two\": 2, \"three\": 3}";

        var lexer = Lexer.newInstance(input);
        var parser = Parser.newInstance(lexer);
        var programNode = parser.parseProgram();

        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        var statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof HashLiteralExpressionNode);

        var hashNode = (HashLiteralExpressionNode) statementNode.expressionNode();
        assertEquals(3, hashNode.pairs().size());

        var expectedMap = Map.of("one", 1, "two", 2, "three", 3);

        hashNode.pairs().forEach((key, value) -> {
            assertTrue(key instanceof StringLiteralExpressionNode);
            var stringKey = (StringLiteralExpressionNode) key;
            var expectedValue = expectedMap.get(stringKey.toString());

            testIntegerLiteral(expectedValue, value);
        });
    }

    @Test
    public void test$emptyHashLiteral() {
        var input = "{}";

        var lexer = Lexer.newInstance(input);
        var parser = Parser.newInstance(lexer);
        var programNode = parser.parseProgram();

        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        var statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof HashLiteralExpressionNode);

        var hashNode = (HashLiteralExpressionNode) statementNode.expressionNode();
        assertEquals(0, hashNode.pairs().size());
    }

    @Test
    public void test$hashLiteralWithExpressions() {
        var input = "{\"one\": 0 + 1, \"two\": 10 - 8, \"three\": 15 / 5}";

        var lexer = Lexer.newInstance(input);
        var parser = Parser.newInstance(lexer);
        var programNode = parser.parseProgram();

        checkParserErrors(parser);

        assertEquals(1, programNode.statementNodes().size());
        assertTrue(programNode.statementNodes().get(0) instanceof ExpressionStatementNode);

        var statementNode = (ExpressionStatementNode) programNode.statementNodes().get(0);

        assertTrue(statementNode.expressionNode() instanceof HashLiteralExpressionNode);

        var hashNode = (HashLiteralExpressionNode) statementNode.expressionNode();
        assertEquals(3, hashNode.pairs().size());

        Map<String, Consumer<ExpressionNode>> testFunctionMap = Map.of(
                "one", (ExpressionNode expression) -> testInfixExpression(0, "+", 1, expression),
                "two", (ExpressionNode expression) -> testInfixExpression(10, "-", 8, expression),
                "three", (ExpressionNode expression) -> testInfixExpression(15, "/", 5, expression)
        );

        hashNode.pairs().forEach((key, value) -> {
            assertTrue(key instanceof StringLiteralExpressionNode);
            var stringKey = (StringLiteralExpressionNode) key;

            assertTrue(testFunctionMap.containsKey(stringKey.toString()));

            testFunctionMap.get(stringKey.toString()).accept(value);
        });
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

    private void testInfixExpression(Object expectedLeft, String expectedOperator, Object expectedRight, ExpressionNode expressionNode) {
        assertTrue(expressionNode instanceof InfixExpressionNode);

        InfixExpressionNode infixExpressionNode = (InfixExpressionNode) expressionNode;

        testLiteralExpression(expectedLeft, infixExpressionNode.left());
        assertEquals(expectedOperator, infixExpressionNode.operator());
        testLiteralExpression(expectedRight, infixExpressionNode.right());
    }

    private void testBooleanLiteral(boolean expectedValue, ExpressionNode expressionNode) {
        assertTrue(expressionNode instanceof BooleanExpressionNode);

        BooleanExpressionNode booleanExpressionNode = (BooleanExpressionNode) expressionNode;

        assertEquals(expectedValue, booleanExpressionNode.value());
        assertEquals(String.valueOf(expectedValue), booleanExpressionNode.tokenLiteral());
    }

    private void testIdentifier(String expectedValue, ExpressionNode expressionNode) {
        assertTrue(expressionNode instanceof IdentifierExpressionNode);
        IdentifierExpressionNode identifierExpressionNode = (IdentifierExpressionNode) expressionNode;
        assertEquals(expectedValue, identifierExpressionNode.value());
        assertEquals(expectedValue, identifierExpressionNode.tokenLiteral());
    }


    private void testLiteralExpression(Object expectedValue, ExpressionNode expressionNode) {
        switch (expectedValue.getClass().getSimpleName()) {
            case "Integer" -> testIntegerLiteral((Integer) expectedValue, expressionNode);
            case "String" -> testIdentifier((String) expectedValue, expressionNode);
            case "Boolean" -> testBooleanLiteral((Boolean) expectedValue, expressionNode);
            default -> fail();
        }
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
