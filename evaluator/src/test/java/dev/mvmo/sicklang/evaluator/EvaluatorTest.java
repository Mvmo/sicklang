package dev.mvmo.sicklang.evaluator;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.internal.env.SickEnvironment;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.array.ArrayObject;
import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;
import dev.mvmo.sicklang.internal.object.function.FunctionObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.internal.object.string.StringObject;
import dev.mvmo.sicklang.parser.Parser;
import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.*;

public class EvaluatorTest {

    private static record SimpleTestCase<E>(String input, E expected) {
    }

    @Test
    public void test$evalIntegerExpressions() {
        Stream.of(
                new SimpleTestCase<>("5", 5),
                new SimpleTestCase<>("10", 10),
                new SimpleTestCase<>("-10", -10),
                new SimpleTestCase<>("-5", -5),
                new SimpleTestCase<>("5 + 5 + 5 + 5 - 10", 10),
                new SimpleTestCase<>("2 * 2 * 2 * 2 * 2", 32),
                new SimpleTestCase<>("-50 + 100 + -50", 0),
                new SimpleTestCase<>("5 * 2 + 10", 20),
                new SimpleTestCase<>("5 + 2 * 10", 25),
                new SimpleTestCase<>("20 + 2 * -10", 0),
                new SimpleTestCase<>("50 / 2 * 2 + 10", 60),
                new SimpleTestCase<>("2 * (5 + 10)", 30),
                new SimpleTestCase<>("3 * 3 * 3 + 10", 37),
                new SimpleTestCase<>("3 * (3 * 3) + 10", 37),
                new SimpleTestCase<>("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50)
        ).forEach(testCase -> {
            var evaluated = testEval(testCase.input);
            testIntegerObject(evaluated, testCase.expected);
        });
    }

    @Test
    public void test$evalBooleanExpressions() {
        Stream.of(
                new SimpleTestCase<>("true", true),
                new SimpleTestCase<>("false", false),
                new SimpleTestCase<>(" 1 < 2", true),
                new SimpleTestCase<>("1 > 2", false),
                new SimpleTestCase<>("1 < 1", false),
                new SimpleTestCase<>("1 > 1", false),
                new SimpleTestCase<>("1 == 1", true),
                new SimpleTestCase<>("1 != 1", false),
                new SimpleTestCase<>("1 == 2", false),
                new SimpleTestCase<>("1 != 2", true),
                new SimpleTestCase<>("true == true", true),
                new SimpleTestCase<>("false == false", true),
                new SimpleTestCase<>("true == false", false),
                new SimpleTestCase<>("true != false", true),
                new SimpleTestCase<>("false != true", true),
                new SimpleTestCase<>("(1 < 2) == true", true),
                new SimpleTestCase<>("(1 < 2) == false", false),
                new SimpleTestCase<>("(1 > 2) == true", false),
                new SimpleTestCase<>("(1 > 2) == false", true)
        ).forEach(testCase -> {
            var evaluated = testEval(testCase.input);
            testBooleanObject(evaluated, testCase.expected);
        });
    }

    @Test
    public void test$evalBangOperator() {
        Stream.of(
                new SimpleTestCase<>("!true", false),
                new SimpleTestCase<>("!false", true),
                new SimpleTestCase<>("!5", false),
                new SimpleTestCase<>("!!true", true),
                new SimpleTestCase<>("!!false", false),
                new SimpleTestCase<>("!!5", true)
        ).forEach(testCase -> {
            var evaluated = testEval(testCase.input);
            testBooleanObject(evaluated, testCase.expected);
        });
    }

    @Test
    public void test$evalIfElseExpressions() {
        Stream.of(
                new SimpleTestCase<>("if (true) { 10 }", 10),
                new SimpleTestCase<>("if (false) { 10 }", null),
                new SimpleTestCase<>("if (1) { 10 }", 10),
                new SimpleTestCase<>("if (1 < 2) { 10 }", 10),
                new SimpleTestCase<>("if (1 > 2) { 10 }", null),
                new SimpleTestCase<>("if (1 > 2) { 10 } else { 20 }", 20),
                new SimpleTestCase<>("if (1 < 2) { 10 } else { 20 }", 10)
        ).forEach(testCase -> {
            var evaluated = testEval(testCase.input);

            if (testCase.expected instanceof Integer i)
                testIntegerObject(evaluated, i);
            else
                testNullObject(evaluated);
        });
    }

    @Test
    public void test$evaluateReturnStatement() {
        Stream.of(
                new SimpleTestCase<>("return 10;", 10),
                new SimpleTestCase<>("return 10; 9;", 10),
                new SimpleTestCase<>("return 2 * 5; 9;", 10),
                new SimpleTestCase<>("9; return 2 * 5; 9", 10),
                new SimpleTestCase<>("""
                        if (10 > 1) {
                            if (10 > 1) {
                                return 10;
                            }
                            return 1;
                        }
                        """, 10)
        ).forEach(testCase -> {
            var evaluated = testEval(testCase.input);
            testIntegerObject(evaluated, testCase.expected);
        });
    }

    @Test
    public void test$evalLetStatement() {
        Stream.of(
                new SimpleTestCase<>("let a = 5; a;", 5),
                new SimpleTestCase<>("let a = 5 * 5; a;", 25),
                new SimpleTestCase<>("let a = 5; let b = a; b", 5),
                new SimpleTestCase<>("let a = 5; let b = a; let c = a + b; c;", 10)
        ).forEach(testCase -> {
            var evaluated = testEval(testCase.input);
            testIntegerObject(evaluated, testCase.expected);
        });
    }

    @Test
    public void test$errorHandling() {
        Stream.of(
                new SimpleTestCase<>("5 + true;", "type mismatch: INTEGER + BOOLEAN"),
                new SimpleTestCase<>("5 + true; 5;", "type mismatch: INTEGER + BOOLEAN"),
                new SimpleTestCase<>("-true", "unknown operator: -BOOLEAN"),
                new SimpleTestCase<>("true + false;", "unknown operator: BOOLEAN + BOOLEAN"),
                new SimpleTestCase<>("5; true + false; 5", "unknown operator: BOOLEAN + BOOLEAN"),
                new SimpleTestCase<>("if (10 > 1) { true + false; }", "unknown operator: BOOLEAN + BOOLEAN"),
                new SimpleTestCase<>("""
                        if (10 > 1) {
                            if (10 > 1) {
                                return true + false;
                            }
                            return 1;
                        }
                        """, "unknown operator: BOOLEAN + BOOLEAN"),
                new SimpleTestCase<>("foobar;", "identifier not found: foobar"),
                new SimpleTestCase<>("\"Hello\" - \"World\"", "unknown operator: STRING - STRING")
        ).forEach(testCase -> {
            var evaluated = testEval(testCase.input);
            assertTrue(evaluated instanceof ErrorObject);
            var errorObject = (ErrorObject) evaluated;
            assertEquals(testCase.expected, errorObject.message());
        });
    }

    @Test
    public void test$functionObject() {
        String input = "fn(x) { x + 2; };";

        var evaluated = testEval(input);
        assertTrue(evaluated instanceof FunctionObject);

        var functionObject = (FunctionObject) evaluated;

        assertEquals(1, functionObject.parameters().size());
        assertEquals("x", functionObject.parameters().get(0).toString());
        assertEquals("(x + 2)", functionObject.body().toString());
    }

    @Test
    public void test$evalFunction() {
        Stream.of(
                new SimpleTestCase<>("let identity = fn(x) { x; }; identity(5);", 5),
                new SimpleTestCase<>("let identity = fn(x) { return x; }; identity(5);", 5),
                new SimpleTestCase<>("let double = fn(x) { x * 2; }; double(5);", 10),
                new SimpleTestCase<>("let add = fn(x, y) { x + y; }; add(5, 5);", 10),
                new SimpleTestCase<>("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20),
                new SimpleTestCase<>("fn(x) { x; }(5)", 5)
        ).forEach(testCase -> testIntegerObject(testEval(testCase.input), testCase.expected));
    }

    @Test
    public void test$closures() {
        var input = "let a = fn(b) { fn (c) { b + c }; }; let d = a(2); d(2)";

        var evaluated = testEval(input);
        int expected = 4;

        testIntegerObject(evaluated, expected);
    }

    @Test
    public void test$stringLiterals() {
        var input = "\"Hello, World!\"";

        var evaluated = testEval(input);
        assertTrue(evaluated instanceof StringObject);
        assertEquals("Hello, World!", ((StringObject) evaluated).value());
    }

    @Test
    public void test$stringConcatenation() {
        var input = "\"Hello\" + \",\" +  \" \" + \"World!\"";

        var evaluated = testEval(input);
        assertTrue(evaluated instanceof StringObject);
        assertEquals("Hello, World!", ((StringObject) evaluated).value());
    }

    // TODO fix order of params rofl
    @Test
    public void test$builtinFunctions() {
        Stream.of(
                new SimpleTestCase<>("len(\"\")", 0),
                new SimpleTestCase<>("len(\"four\")", 4),
                new SimpleTestCase<>("len(\"hello world\")", 11),
                new SimpleTestCase<>("len(1)", "argument to `len` not supported. got INTEGER"),
                new SimpleTestCase<>("len(\"one\", \"two\")", "wrong number of arguments. got=2, want=1")
        ).forEach(testCase -> {
            var evaluated = testEval(testCase.input);

            if (testCase.expected instanceof Integer expectedInt) {
                testIntegerObject(evaluated, expectedInt);
                return;
            }

            if (testCase.expected instanceof String expectedString) {
                assertTrue(evaluated instanceof ErrorObject);
                assertEquals(expectedString, ((ErrorObject) evaluated).message());
            }
        });
    }

    @Test
    public void test$arrayLiterals() {
        var input = "[1, 2 * 2, 3 + 3]";

        var evaluated = testEval(input);
        assertTrue(evaluated instanceof ArrayObject);

        var array = (ArrayObject) evaluated;
        assertEquals(3, array.elements().size());

        testIntegerObject(array.elements().get(0), 1);
        testIntegerObject(array.elements().get(1), 4);
        testIntegerObject(array.elements().get(2), 6);
    }

    private SickObject testEval(String input) {
        var lexer = Lexer.newInstance(input);
        var parser = Parser.newInstance(lexer);
        var programNode = parser.parseProgram();
        var environment = SickEnvironment.newInstance();

        return SicklangEvaluator.eval(programNode, environment);
    }

    private void testIntegerObject(SickObject object, int expected) {
        assertTrue(object instanceof IntegerObject);

        var integerObject = (IntegerObject) object;
        assertEquals(expected, integerObject.value());
    }

    private void testBooleanObject(SickObject object, boolean expected) {
        assertTrue(object instanceof BooleanObject);

        var booleanObject = (BooleanObject) object;
        assertEquals(expected, booleanObject.value());
    }

    private void testNullObject(SickObject object) {
        if (object != NullObject.NULL)
            fail("Object is not null");
    }

}
