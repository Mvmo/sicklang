package dev.mvmo.sicklang.evaluator;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.internal.object.SickObject;
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
                new TestCase("10", 10)
        };

        for (TestCase testCase : testCases) {
            SickObject evaluated = testEval(testCase.input);
            testIntegerObject(evaluated, testCase.expected);
        }
    }

    private SickObject testEval(String input) {
        Lexer lexer = Lexer.newInstance(input);
        Parser parser = Parser.newInstance(lexer);

        ProgramNode programNode = parser.parseProgram();

        return SicklangEvaluator.eval(programNode);
    }

    public void testIntegerObject(SickObject object, int expected) {
        assertTrue(object instanceof IntegerObject);

        IntegerObject integerObject = (IntegerObject) object;
        assertEquals(integerObject.value(), expected);
    }

}
