package dev.mvmo.sicklang.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.parser.ast.expression.*;
import dev.mvmo.sicklang.parser.ast.function.InfixParseFunction;
import dev.mvmo.sicklang.parser.ast.function.PrefixParseFunction;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.*;
import dev.mvmo.sicklang.parser.precedence.Precedence;
import dev.mvmo.sicklang.token.Token;
import dev.mvmo.sicklang.token.TokenType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Parser {

    private final Lexer lexer;

    private final Map<TokenType, PrefixParseFunction> prefixParseFunctionMap;
    private final Map<TokenType, InfixParseFunction> infixParseFunctionMap;

    private final List<String> errorMessages;

    private Token currentToken;
    private Token peekToken;

    public static Parser newInstance(Lexer lexer) {
        var parser = new Parser(lexer, Maps.newHashMap(), Maps.newHashMap(), Lists.newArrayList());

        parser.prefixParseFunctionMap.put(TokenType.IDENTIFIER, parser::parseIdentifier);
        parser.prefixParseFunctionMap.put(TokenType.INTEGER, parser::parseIntegerLiteral);
        parser.prefixParseFunctionMap.put(TokenType.BANG, parser::parsePrefixExpression);
        parser.prefixParseFunctionMap.put(TokenType.MINUS, parser::parsePrefixExpression);
        parser.prefixParseFunctionMap.put(TokenType.TRUE, parser::parseBooleanExpression);
        parser.prefixParseFunctionMap.put(TokenType.FALSE, parser::parseBooleanExpression);
        parser.prefixParseFunctionMap.put(TokenType.LEFT_PAREN, parser::parseGroupedExpression);
        parser.prefixParseFunctionMap.put(TokenType.IF, parser::parseIfExpression);
        parser.prefixParseFunctionMap.put(TokenType.FUNCTION, parser::parseFunctionExpression);
        parser.prefixParseFunctionMap.put(TokenType.STRING, parser::parseStringLiteralExpression);

        parser.infixParseFunctionMap.put(TokenType.PLUS, parser::parseInfixExpression);
        parser.infixParseFunctionMap.put(TokenType.MINUS, parser::parseInfixExpression);
        parser.infixParseFunctionMap.put(TokenType.SLASH, parser::parseInfixExpression);
        parser.infixParseFunctionMap.put(TokenType.ASTERISK, parser::parseInfixExpression);
        parser.infixParseFunctionMap.put(TokenType.EQUALS, parser::parseInfixExpression);
        parser.infixParseFunctionMap.put(TokenType.NOT_EQUALS, parser::parseInfixExpression);
        parser.infixParseFunctionMap.put(TokenType.LESS_THAN, parser::parseInfixExpression);
        parser.infixParseFunctionMap.put(TokenType.GREATER_THAN, parser::parseInfixExpression);
        parser.infixParseFunctionMap.put(TokenType.LEFT_PAREN, parser::parseCallExpression);

        parser.nextToken();
        parser.nextToken();

        return parser;
    }

    public void nextToken() {
        currentToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public ProgramNode parseProgram() {
        var programNode = ProgramNode.newInstance();

        while (!currentTokenIs(TokenType.EOF)) {
            var statementNode = parseStatement();
            if (statementNode != null)
                programNode.statementNodes().add(statementNode);

            nextToken();
        }

        return programNode;
    }

    private StatementNode parseStatement() {
        return switch (currentToken.type()) {
            case LET -> parseLetStatement();
            case RETURN -> parseReturnStatement();
            default -> parseExpressionStatement();
        };
    }

    public LetStatementNode parseLetStatement() {
        var statementNode = LetStatementNode.newInstance(currentToken);
        if (!expectPeek(TokenType.IDENTIFIER))
            return null;

        statementNode.identifier(IdentifierExpressionNode.newInstance(currentToken, currentToken.literal()));

        if (!expectPeek(TokenType.ASSIGN))
            return null;

        nextToken();

        statementNode.value(parseExpression(Precedence.LOWEST));

        if (peekTokenIs(TokenType.SEMICOLON))
            nextToken();

        return statementNode;
    }

    public ReturnStatementNode parseReturnStatement() {
        var statementNode = ReturnStatementNode.newInstance(currentToken);
        nextToken();

        statementNode.returnValue(parseExpression(Precedence.LOWEST));

        if (peekTokenIs(TokenType.SEMICOLON))
            nextToken();

        return statementNode;
    }

    public ExpressionStatementNode parseExpressionStatement() {
        var statementNode = ExpressionStatementNode.newInstance(currentToken);

        statementNode.expressionNode(parseExpression(Precedence.LOWEST));

        if (peekTokenIs(TokenType.SEMICOLON))
            nextToken();

        return statementNode;
    }

    public ExpressionNode parseExpression(Precedence precedence) {
        var prefixParseFunction = prefixParseFunctionMap.get(currentToken.type());
        if (prefixParseFunction == null) {
            noPrefixParseFunctionError(currentToken.type());
            return null;
        }

        var leftExpression = prefixParseFunction.parse();

        while (!peekTokenIs(TokenType.SEMICOLON) && precedence.ordinal() < Precedence.findPrecedence(peekToken.type()).ordinal()) {
            var infixParseFunction = infixParseFunctionMap.get(peekToken.type());
            if (infixParseFunction == null) {
                return leftExpression;
            }

            nextToken();

            leftExpression = infixParseFunction.parse(leftExpression);
        }

        return leftExpression;
    }

    public IdentifierExpressionNode parseIdentifier() {
        return IdentifierExpressionNode.newInstance(currentToken, currentToken.literal());
    }

    public IntegerLiteralExpressionNode parseIntegerLiteral() {
        var literalExpressionNode = IntegerLiteralExpressionNode.newInstance(currentToken);

        try {
            int integer = Integer.parseInt(currentToken.literal());
            literalExpressionNode.value(integer);
        } catch (NumberFormatException exception) {
            var errorMsg = String.format("could not parse %s as an integer", currentToken.literal());
            errorMessages.add(errorMsg);

            return null;
        }

        return literalExpressionNode;
    }

    public ExpressionNode parsePrefixExpression() {
        var prefixExpressionNode = PrefixExpressionNode.newInstance(currentToken, currentToken.literal());

        nextToken();

        prefixExpressionNode.right(parseExpression(Precedence.PREFIX));

        return prefixExpressionNode;
    }

    public ExpressionNode parseInfixExpression(ExpressionNode left) {
        var infixExpressionNode = InfixExpressionNode.newInstance(currentToken, left, currentToken.literal());
        var precedence = Precedence.findPrecedence(currentToken.type());

        nextToken();

        infixExpressionNode.right(parseExpression(precedence));

        return infixExpressionNode;
    }

    public BooleanExpressionNode parseBooleanExpression() {
        return BooleanExpressionNode.newInstance(currentToken, currentTokenIs(TokenType.TRUE));
    }

    public ExpressionNode parseGroupedExpression() {
        nextToken();

        var expressionNode = parseExpression(Precedence.LOWEST);

        if (!expectPeek(TokenType.RIGHT_PAREN))
            return null;

        return expressionNode;
    }

    public ExpressionNode parseIfExpression() {
        var expressionNode = IfExpressionNode.newInstance(currentToken);

        if (!expectPeek(TokenType.LEFT_PAREN))
            return null;

        nextToken();

        expressionNode.conditionalExpressionNode(parseExpression(Precedence.LOWEST));

        if (!expectPeek(TokenType.RIGHT_PAREN))
            return null;

        if (!expectPeek(TokenType.LEFT_BRACE))
            return null;

        expressionNode.consequence(parseBlockStatement());

        if (peekTokenIs(TokenType.ELSE)) {
            nextToken();

            if (!expectPeek(TokenType.LEFT_BRACE))
                return null;

            expressionNode.alternative(parseBlockStatement());
        }

        return expressionNode;
    }

    public BlockStatementNode parseBlockStatement() {
        var blockStatementNode = BlockStatementNode.newInstance(currentToken);

        nextToken();

        while (!currentTokenIs(TokenType.RIGHT_BRACE) && !currentTokenIs(TokenType.EOF)) {
            var statement = parseStatement();
            if (statement != null)
                blockStatementNode.statementNodes().add(statement);

            nextToken();
        }

        return blockStatementNode;
    }

    public FunctionLiteralExpressionNode parseFunctionExpression() {
        var functionLiteralExpressionNode = FunctionLiteralExpressionNode.newInstance(currentToken);

        if (!expectPeek(TokenType.LEFT_PAREN))
            return null;

        functionLiteralExpressionNode.parameters(parseFunctionParameters());

        if (!expectPeek(TokenType.LEFT_BRACE))
            return null;

        functionLiteralExpressionNode.body(parseBlockStatement());

        return functionLiteralExpressionNode;
    }

    public List<IdentifierExpressionNode> parseFunctionParameters() {
        List<IdentifierExpressionNode> identifiers = Lists.newArrayList();

        if (peekTokenIs(TokenType.RIGHT_PAREN)) {
            nextToken();
            return identifiers;
        }

        nextToken();

        identifiers.add(IdentifierExpressionNode.newInstance(currentToken, currentToken.literal()));
        // use do while
        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();
            nextToken();

            identifiers.add(IdentifierExpressionNode.newInstance(currentToken, currentToken.literal()));
        }

        if (!expectPeek(TokenType.RIGHT_PAREN))
            return null;

        return identifiers;
    }

    public ExpressionNode parseCallExpression(ExpressionNode function) {
        var callExpressionNode = CallExpressionNode.newInstance(currentToken, function);
        callExpressionNode.arguments(parseCallArguments());

        return callExpressionNode;
    }

    public List<ExpressionNode> parseCallArguments() {
        List<ExpressionNode> argumentList = Lists.newArrayList();

        if (peekTokenIs(TokenType.RIGHT_PAREN)) {
            nextToken();
            return argumentList;
        }

        nextToken();

        argumentList.add(parseExpression(Precedence.LOWEST));

        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();
            nextToken();

            argumentList.add(parseExpression(Precedence.LOWEST));
        }

        if (!expectPeek(TokenType.RIGHT_PAREN))
            return null;

        return argumentList;
    }

    // TODO: private access
    public StringLiteralExpression parseStringLiteralExpression() {
        return StringLiteralExpression.newInstance(currentToken, currentToken.literal());
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
        } else {
            peekError(tokenType);
            return false;
        }
    }

    private void noPrefixParseFunctionError(TokenType type) {
        String message = String.format("no prefix parse function found for type %s", type.name());
        errorMessages.add(message);
    }

    private void peekError(TokenType tokenType) {
        String message = String.format("expected next token to be %s, got %s instead", tokenType, peekToken.type());
        errorMessages.add(message);
    }

}
