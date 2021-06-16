package dev.mvmo.sicklang.parser.test;

import dev.mvmo.sicklang.parser.ast.expression.IdentifierExpressionNode;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.LetStatementNode;
import dev.mvmo.sicklang.token.Token;
import dev.mvmo.sicklang.token.TokenType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AbstractSyntaxTreeTest {

    @Test
    public void test$abstractSyntaxTreeString() {
        ProgramNode programNode = ProgramNode.newInstance();


        LetStatementNode letStatementNode = LetStatementNode.newInstance(new Token(TokenType.LET, "let"));
        letStatementNode.identifier(IdentifierExpressionNode.newInstance(new Token(TokenType.IDENTIFIER, "myVar"), "myVar"));
        letStatementNode.value(IdentifierExpressionNode.newInstance(new Token(TokenType.IDENTIFIER, "anotherVar"), "anotherVar"));

        programNode.statementNodes().add(letStatementNode);

        assertEquals("let myVar = anotherVar;", programNode.toString());
    }

}
