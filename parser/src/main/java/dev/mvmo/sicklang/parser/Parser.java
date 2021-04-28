package dev.mvmo.sicklang.parser;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.parser.ast.expression.IdentifierExpressionNode;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.LetStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.StatementNode;
import dev.mvmo.sicklang.token.Token;
import dev.mvmo.sicklang.token.TokenType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Parser {

    private final Lexer lexer;

    private Token currentToken;
    private Token peekToken;

    public static Parser newInstance(Lexer lexer) {
        Parser parser = new Parser(lexer);

        parser.nextToken();
        parser.nextToken();

        return parser;
    }

    public void nextToken() {
        currentToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public ProgramNode parseProgram() {
        ProgramNode programNode = ProgramNode.newInstance();

        while (!currentToken.type().equals(TokenType.EOF)) {
            StatementNode statementNode = parseStatement();
            if (statementNode != null)
                programNode.statementNodes().add(statementNode);

            nextToken();
        }

        return programNode;
    }

    private StatementNode parseStatement() {
        switch (currentToken.type()) {
            case LET:
                return parseLetStatement();
            default:
                return null;
        }
    }

    public LetStatementNode parseLetStatement() {
        LetStatementNode statementNode = LetStatementNode.newInstance(currentToken);
        if (!expectPeek(TokenType.IDENTIFIER)) {
            return null;
        }

        statementNode.identifier(IdentifierExpressionNode.newInstance(currentToken, currentToken.literal()));

        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }

        // TODO: were' skipping the the expression until we encounter a semicolon
        while (!currentTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return statementNode;
    }

    private boolean currentTokenIs(TokenType tokenType) {
        return currentToken.type().equals(tokenType);
    }

    private boolean peekTokenIs(TokenType tokenType) {
        return peekToken.type().equals(tokenType);
    }

    private boolean expectPeek(TokenType tokenType) {
        if (peekTokenIs(tokenType)) {
            nextToken();
            return true;
        } else
            return false;
    }

}
