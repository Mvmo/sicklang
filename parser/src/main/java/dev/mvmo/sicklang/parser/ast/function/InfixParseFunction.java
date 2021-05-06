package dev.mvmo.sicklang.parser.ast.function;

import dev.mvmo.sicklang.parser.ast.expression.ExpressionNode;

public interface InfixParseFunction {

    ExpressionNode parse(ExpressionNode leftExpression);

}
