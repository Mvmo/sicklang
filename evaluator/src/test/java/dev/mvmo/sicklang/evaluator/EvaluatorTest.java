package dev.mvmo.sicklang.evaluator;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.parser.Parser;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EvaluatorTest {

    @Test
    public void test$evalIntegerExpression() {
        record TestCase(String input, int expected) {
        }

        TestCase[] testCases = new TestCase[]{
                new TestCase("5", 5),
                new TestCase("10", 10),
                new TestCase("-10", -10),
                new TestCase("-5", -5)
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
                new TestCase("false", false)
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

}
