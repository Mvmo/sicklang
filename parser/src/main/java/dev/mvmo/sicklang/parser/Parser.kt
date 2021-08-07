package dev.mvmo.sicklang.parser;

import dev.mvmo.sicklang.Lexer
import dev.mvmo.sicklang.parser.ast.expression.*
import dev.mvmo.sicklang.parser.ast.function.InfixParseFunction
import dev.mvmo.sicklang.parser.ast.function.PrefixParseFunction
import dev.mvmo.sicklang.parser.ast.program.ProgramNode
import dev.mvmo.sicklang.parser.ast.statement.*
import dev.mvmo.sicklang.parser.precedence.Precedence
import dev.mvmo.sicklang.token.Token
import dev.mvmo.sicklang.token.TokenType

class Parser(private val lexer: Lexer) {

    private val prefixParseFunctionMap = mutableMapOf<TokenType, PrefixParseFunction>()
    private val infixParseFunctionMap = mutableMapOf<TokenType, InfixParseFunction>()

    val errorMessages: MutableList<String> = arrayListOf()

    private var currentToken: Token
    private var peekToken: Token

    init {
        with(prefixParseFunctionMap) {
            put(TokenType.IDENTIFIER, PrefixParseFunction { parseIdentifier() })
            put(TokenType.INTEGER, PrefixParseFunction { parseIntegerLiteral() })
            put(TokenType.BANG, PrefixParseFunction { parsePrefixExpression() })
            put(TokenType.MINUS, PrefixParseFunction { parsePrefixExpression() })
            put(TokenType.TRUE, PrefixParseFunction { parseBooleanExpression() })
            put(TokenType.FALSE, PrefixParseFunction { parseBooleanExpression() })
            put(TokenType.LEFT_PAREN, PrefixParseFunction { parseGroupedExpression() })
            put(TokenType.IF, PrefixParseFunction { parseIfExpression() })
            put(TokenType.FUNCTION, PrefixParseFunction { parseFunctionExpression() })
            put(TokenType.STRING, PrefixParseFunction { parseStringLiteralExpression() })
            put(TokenType.LEFT_BRACKET, PrefixParseFunction { parseArrayLiteralExpression() })
            put(TokenType.LEFT_BRACE, PrefixParseFunction { parseHashLiteral() })
        }

        with(infixParseFunctionMap) {
            put(TokenType.PLUS, InfixParseFunction { parseInfixExpression(it) })
            put(TokenType.MINUS, InfixParseFunction { parseInfixExpression(it) })
            put(TokenType.SLASH, InfixParseFunction { parseInfixExpression(it) })
            put(TokenType.ASTERISK, InfixParseFunction { parseInfixExpression(it) })
            put(TokenType.EQUALS, InfixParseFunction { parseInfixExpression(it) })
            put(TokenType.NOT_EQUALS, InfixParseFunction { parseInfixExpression(it) })
            put(TokenType.LESS_THAN, InfixParseFunction { parseInfixExpression(it) })
            put(TokenType.GREATER_THAN, InfixParseFunction { parseInfixExpression(it) })
            put(TokenType.LEFT_PAREN, InfixParseFunction { parseCallExpression(it) })
            put(TokenType.LEFT_BRACKET, InfixParseFunction { parseIndexExpression(it) })
        }

        this.currentToken = lexer.nextToken()
        this.peekToken = currentToken

        nextToken()
    }

    fun nextToken() {
        this.currentToken = peekToken
        this.peekToken = lexer.nextToken()
    }

    fun parseProgram(): ProgramNode {
        val programNode = ProgramNode()

        while (!currentTokenIs(TokenType.EOF)) {
            val statementNode = parseStatement()
            programNode.statementNodes.add(statementNode);

            nextToken();
        }

        return programNode;
    }

    fun parseStatement(): StatementNode {
        return when (currentToken.type()) {
            TokenType.LET -> parseLetStatement()
            TokenType.RETURN -> parseReturnStatement()
            else -> parseExpressionStatement()
        }
    }

    fun parseLetStatement(): LetStatementNode {
        val statementNode = LetStatementNode(currentToken)
        if (!expectPeek(TokenType.IDENTIFIER))
            throw SickParseException("unexpected token. was looking for identifier")

        statementNode.identifier = IdentifierExpressionNode(currentToken, currentToken.literal())

        if (!expectPeek(TokenType.ASSIGN))
            throw SickParseException("unexpected token. was looking for assign")

        nextToken()

        statementNode.value = parseExpression(Precedence.LOWEST)

        if (peekTokenIs(TokenType.SEMICOLON))
            nextToken()

        return statementNode;
    }

    fun parseReturnStatement(): ReturnStatementNode {
        val statementNode = ReturnStatementNode(currentToken)
        nextToken()

        statementNode.returnValue = parseExpression(Precedence.LOWEST)

        if (peekTokenIs(TokenType.SEMICOLON))
            nextToken()

        return statementNode;
    }

    fun parseExpressionStatement(): ExpressionStatementNode {
        val statementNode = ExpressionStatementNode(currentToken)

        statementNode.expressionNode = parseExpression(Precedence.LOWEST)

        if (peekTokenIs(TokenType.SEMICOLON))
            nextToken()

        return statementNode
    }

    fun parseExpression(precedence: Precedence): ExpressionNode {
        val prefixParseFunction = prefixParseFunctionMap[currentToken.type()]
            ?: throw SickParseException("No prefix parse function found for ${currentToken.type}")

        var leftExpression = prefixParseFunction.parse()

        while (!peekTokenIs(TokenType.SEMICOLON) && precedence.ordinal < Precedence.findPrecedence(peekToken.type()).ordinal) {
            val infixParseFunction = infixParseFunctionMap[peekToken.type()]
                ?: return leftExpression

            nextToken();

            leftExpression = infixParseFunction.parse(leftExpression)
        }

        return leftExpression;
    }

    fun parseIdentifier(): IdentifierExpressionNode {
        return IdentifierExpressionNode(currentToken, currentToken.literal());
    }

    fun parseIntegerLiteral(): IntegerLiteralExpressionNode {
        val literalExpressionNode = IntegerLiteralExpressionNode(currentToken)

        try {
            val integer = currentToken.literal().toInt()
            literalExpressionNode.value = integer
        } catch (_: NumberFormatException) {
            throw SickParseException("Couldn't convert '${currentToken.literal}' to int")
        }

        return literalExpressionNode
    }

    fun parsePrefixExpression(): ExpressionNode {
        val prefixExpressionNode = PrefixExpressionNode(currentToken, currentToken.literal())

        nextToken()
        prefixExpressionNode.right = parseExpression(Precedence.PREFIX)

        return prefixExpressionNode
    }

    fun parseInfixExpression(left: ExpressionNode): ExpressionNode {
        val infixExpressionNode = InfixExpressionNode(currentToken, left, currentToken.literal())
        val precedence = Precedence.findPrecedence(currentToken.type())

        nextToken();

        infixExpressionNode.right = parseExpression(precedence)

        return infixExpressionNode
    }

    fun parseBooleanExpression(): BooleanExpressionNode {
        return BooleanExpressionNode(currentToken, currentTokenIs(TokenType.TRUE))
    }

    fun parseGroupedExpression(): ExpressionNode {
        nextToken()

        val expressionNode = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RIGHT_PAREN))
            throw SickParseException("Expected peek token to be right paren")

        return expressionNode
    }

    fun parseIfExpression(): ExpressionNode {
        val expressionNode = IfExpressionNode(currentToken)

        if (!expectPeek(TokenType.LEFT_PAREN))
            throw SickParseException("Expected peek token to be left paren")

        nextToken()

        expressionNode.conditionalExpressionNode = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RIGHT_PAREN))
            throw SickParseException("Expected peek token to be right paren")


        nextToken()
        val consequenceNode = if (!currentTokenIs(TokenType.LEFT_BRACE))
            parseSingleLineBlockStatement()
        else
            parseBlockStatement()

        expressionNode.consequence = consequenceNode

        if (peekTokenIs(TokenType.ELSE)) {
            nextToken()
            nextToken()

            val alternativeNode = if (!currentTokenIs(TokenType.LEFT_BRACE))
                parseSingleLineBlockStatement()
            else
                parseBlockStatement()

            expressionNode.alternative = alternativeNode
        }

        return expressionNode;
    }

    fun parseSingleLineBlockStatement(): BlockStatementNode {
        val blockStatementNode = BlockStatementNode(Token(TokenType.LEFT_BRACE, "{"))

        val statement = parseStatement()
        blockStatementNode.statementNodes.add(statement)

        return blockStatementNode;
    }

    fun parseBlockStatement(): BlockStatementNode {
        val blockStatementNode = BlockStatementNode(currentToken)

        nextToken()

        while (!currentTokenIs(TokenType.RIGHT_BRACE) && !currentTokenIs(TokenType.EOF)) {
            val statement = parseStatement()
            blockStatementNode.statementNodes.add(statement)

            nextToken()
        }

        return blockStatementNode
    }

    fun parseFunctionExpression(): FunctionLiteralExpressionNode {
        val functionLiteralExpressionNode = FunctionLiteralExpressionNode(currentToken)

        if (!expectPeek(TokenType.LEFT_PAREN))
            throw SickParseException("Expected peek token to be left paren")

        functionLiteralExpressionNode.parameters = parseFunctionParameters()

        if (!expectPeek(TokenType.LEFT_BRACE))
            throw SickParseException("Expected peek token to be left brace")

        functionLiteralExpressionNode.body = parseBlockStatement()

        return functionLiteralExpressionNode
    }

    fun parseFunctionParameters(): List<IdentifierExpressionNode> {
        val identifiers = arrayListOf<IdentifierExpressionNode>()

        if (peekTokenIs(TokenType.RIGHT_PAREN)) {
            nextToken()
            return identifiers
        }

        nextToken()

        identifiers.add(IdentifierExpressionNode(currentToken, currentToken.literal()))

        while (peekTokenIs(TokenType.COMMA)) {
            nextToken()
            nextToken()

            identifiers.add(IdentifierExpressionNode(currentToken, currentToken.literal()))
        }

        if (!expectPeek(TokenType.RIGHT_PAREN))
            throw SickParseException("Expected peek token to be right paren")

        return identifiers
    }

    fun parseCallExpression(function: ExpressionNode): ExpressionNode {
        val callExpressionNode = CallExpressionNode(currentToken, function)
        callExpressionNode.arguments = parseExpressionList(TokenType.RIGHT_PAREN)

        return callExpressionNode
    }

    fun parseStringLiteralExpression(): StringLiteralExpressionNode {
        return StringLiteralExpressionNode(currentToken, currentToken.literal())
    }

    fun parseArrayLiteralExpression(): ArrayLiteralExpressionNode {
        return ArrayLiteralExpressionNode(currentToken, parseExpressionList(TokenType.RIGHT_BRACKET))
    }

    fun parseIndexExpression(left: ExpressionNode): IndexExpressionNode {
        val startToken = currentToken
        nextToken()
        val indexExpression = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RIGHT_BRACKET))
            throw SickParseException("Expected peek token to be right bracket")

        return IndexExpressionNode(startToken, left, indexExpression)
    }

    fun parseHashLiteral(): HashLiteralExpressionNode {
        val startToken = currentToken
        val pairs = mutableMapOf<ExpressionNode, ExpressionNode>()

        while (!peekTokenIs(TokenType.RIGHT_BRACE)) {
            nextToken();
            val keyExpression = parseExpression(Precedence.LOWEST);

            if (!expectPeek(TokenType.COLON))
                throw SickParseException("Expected peek token to be colon")

            nextToken()

            val valueExpression = parseExpression(Precedence.LOWEST)
            pairs[keyExpression] = valueExpression

            if (!peekTokenIs(TokenType.RIGHT_BRACE) && !expectPeek(TokenType.COMMA))
                throw SickParseException("rofl")
        }

        if (!expectPeek(TokenType.RIGHT_BRACE))
            throw SickParseException("Expected peek token to be right brace")

        return HashLiteralExpressionNode(startToken, pairs)
    }

    fun parseExpressionList(till: TokenType): List<ExpressionNode> {
        val list = arrayListOf<ExpressionNode>()

        if (peekTokenIs(till)) {
            nextToken()
            return list
        }

        nextToken()
        list.add(parseExpression(Precedence.LOWEST))

        while (peekTokenIs(TokenType.COMMA)) {
            nextToken()
            nextToken()

            list.add(parseExpression(Precedence.LOWEST))
        }

        if (!expectPeek(till))
            throw SickParseException("Expected peek token to be $till")

        return list
    }

    fun currentTokenIs(tokenType: TokenType): Boolean {
        return currentToken.type().equals(tokenType);
    }

    fun peekTokenIs(tokenType: TokenType): Boolean {
        return peekToken.type().equals(tokenType);
    }

    fun expectPeek(tokenType: TokenType): Boolean {
        if (peekTokenIs(tokenType)) {
            nextToken()
            return true
        } else {
            peekError(tokenType)
            return false;
        }
    }

    fun noPrefixParseFunctionError(type: TokenType) {
        val message = String.format("no prefix parse function found for type %s", type.name);
        errorMessages.add(message);
    }

    fun peekError(tokenType: TokenType) {
        val message = String.format("expected next token to be %s, got %s instead", tokenType, peekToken.type());
        errorMessages.add(message);
    }

}
