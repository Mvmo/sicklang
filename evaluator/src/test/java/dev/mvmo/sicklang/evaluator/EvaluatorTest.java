package dev.mvmo.sicklang.evaluator;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.parser.Parser;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class EvaluatorTest {

    @Test
    public void test$evalIntegerExpressions() {
        record TestCase(String input, int expected) {
        }

        TestCase[] testCases = new TestCase[]{
                new TestCase("5", 5),
                new TestCase("10", 10),
                new TestCase("-10", -10),
                new TestCase("-5", -5),
                new TestCase("5 + 5 + 5 + 5 - 10", 10),
                new TestCase("2 * 2 * 2 * 2 * 2", 32),
                new TestCase("-50 + 100 + -50", 0),
                new TestCase("5 * 2 + 10", 20),
                new TestCase("5 + 2 * 10", 25),
                new TestCase("20 + 2 * -10", 0),
                new TestCase("50 / 2 * 2 + 10", 60),
                new TestCase("2 * (5 + 10)", 30),
                new TestCase("3 * 3 * 3 + 10", 37),
                new TestCase("3 * (3 * 3) + 10", 37),
                new TestCase("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50)
        };

        for (TestCase testCase : testCases) {
            SickObject evaluated = testEval(testCase.input);
            testIntegerObject(evaluated, testCase.expected);
        }
    }

    @Test
    public void test$evalBooleanExpressions() {
        record TestCase(String input, boolean expected) {
        }

        TestCase[] testCases = new TestCase[]{
                new TestCase("true", true),
                new TestCase("false", false),
                new TestCase(" 1 < 2", true),
                new TestCase("1 > 2", false),
                new TestCase("1 < 1", false),
                new TestCase("1 > 1", false),
                new TestCase("1 == 1", true),
                new TestCase("1 != 1", false),
                new TestCase("1 == 2", false),
                new TestCase("1 != 2", true),
                new TestCase("true == true", true),
                new TestCase("false == false", true),
                new TestCase("true == false", false),
                new TestCase("true != false", true),
                new TestCase("false != true", true),
                new TestCase("(1 < 2) == true", true),
                new TestCase("(1 < 2) == false", false),
                new TestCase("(1 > 2) == true", false),
                new TestCase("(1 > 2) == false", true)
        };

        for (TestCase testCase : testCases) {
            SickObject evaluated = testEval(testCase.input);
            testBooleanObject(evaluated, testCase.expected);
        }
    }

    @Test
    public void test$evalBangOperator() {
        record TestCase(String input, boolean expected) {
        }

        TestCase[] testCases = new TestCase[]{
                new TestCase("!true", false),
                new TestCase("!false", true),
                new TestCase("!5", false),
                new TestCase("!!true", true),
                new TestCase("!!false", false),
                new TestCase("!!5", true)
        };

        for (TestCase testCase : testCases) {
            SickObject evaluated = testEval(testCase.input);
            testBooleanObject(evaluated, testCase.expected);
        }
    }

    @Test
    public void test$ifElseExpressions() {
        record TestCase<T>(String input, T expected) {}

        TestCase<?>[] testCases = new TestCase[] {
                new TestCase<>("if (true) { 10 }", 10),
                new TestCase<>("if (false) { 10 }", null),
                new TestCase<>("if (1) { 10 }", 10),
                new TestCase<>("if (1 < 2) { 10 }", 10),
                new TestCase<>("if (1 > 2) { 10 }", null),
                new TestCase<>("if (1 > 2) { 10 } else { 20 }", 20),
                new TestCase<>("if (1 < 2) { 10 } else { 20 }", 10)
        };

        for (TestCase<?> testCase : testCases) {
            SickObject evaluated = testEval(testCase.input);
            
            if (testCase.expected instanceof Integer i) {
                testIntegerObject(evaluated, i);
            } else {
                testNullObject(evaluated);
            }
        }
    }

    private SickObject testEval(String input) {
        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();

        return SicklangEvaluator.eval(programNode);
    }

    private void testIntegerObject(SickObject object, int expected) {
        assertTrue(object instanceof IntegerObject);

        IntegerObject integerObject = (IntegerObject) object;
        assertEquals(expected, integerObject.value());
    }

    private void testBooleanObject(SickObject object, boolean expected) {
        assertTrue(object instanceof BooleanObject);

        BooleanObject booleanObject = (BooleanObject) object;
        assertEquals(expected, booleanObject.value());
    }

    private void testNullObject(SickObject object) {
        if (object != NullObject.NULL)
            fail("Object is not null");
    }

}
