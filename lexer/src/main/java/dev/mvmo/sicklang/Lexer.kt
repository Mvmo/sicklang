package dev.mvmo.sicklang

import dev.mvmo.sicklang.token.Token
import dev.mvmo.sicklang.token.TokenType

class Lexer(private val input: String) {
    private var position // current position on input (points to current char)
            = 0
    private var readPosition // current reading position in input (after current char)
            = 0
    private var currentChar // current char under examination
            = '\u0000'

    init {
        readChar()
    }

    private fun readChar() {
        currentChar = if (readPosition >= input.length) '\u0000' else input[readPosition]
        position = readPosition
        readPosition += 1
    }

    private fun peekChar(): Char {
        return if (readPosition >= input.length) '\u0000'
        else input[readPosition]
    }

    fun nextToken(): Token {
        skipWhitespace()
        val token: Token = when (currentChar) {
            '=' -> {
                if (peekChar() == '=') {
                    val c = currentChar
                    readChar()
                    val literal = "$c$currentChar"

                    Token(TokenType.EQUALS, literal)
                } else
                    Token(TokenType.ASSIGN, "=")
            }
            '+' -> Token(TokenType.PLUS, "+")
            '-' -> Token(TokenType.MINUS, "-")
            '*' -> Token(TokenType.ASTERISK, "*")
            '/' -> Token(TokenType.SLASH, "/")
            '!' -> {
                if (peekChar() == '=') {
                    val c = currentChar
                    readChar()
                    val literal = "" + c + currentChar
                    Token(TokenType.NOT_EQUALS, literal)
                } else
                    Token(TokenType.BANG, "!")
            }
            '<' -> Token(TokenType.LESS_THAN, "<")
            '>' -> Token(TokenType.GREATER_THAN, ">")
            ';' -> Token(TokenType.SEMICOLON, ";")
            '(' -> Token(TokenType.LEFT_PAREN, "(")
            ')' -> Token(TokenType.RIGHT_PAREN, ")")
            ',' -> Token(TokenType.COMMA, ",")
            '{' -> Token(TokenType.LEFT_BRACE, "{")
            '}' -> Token(TokenType.RIGHT_BRACE, "}")
            '[' -> Token(TokenType.LEFT_BRACKET, "[")
            ']' -> Token(TokenType.RIGHT_BRACKET, "]")
            '"' -> Token(TokenType.STRING, readString())
            ':' -> Token(TokenType.COLON, ":")
            '|' -> {
                if (peekChar() == '|') {
                    val c = currentChar
                    readChar()
                    val literal = "$c$currentChar"
                    Token(TokenType.OR, literal)
                } else
                    Token(TokenType.BITWISE_OR, "|")
            }
            '&' -> {
                if (peekChar() == '&') {
                    val c = currentChar
                    readChar()
                    val literal = "$c$currentChar"
                    Token(TokenType.AND, literal)
                } else
                    Token(TokenType.BITWISE_AND, "&")
            }
            '\u0000' -> Token(TokenType.EOF, "")
            else -> return findToken()
        }
        readChar()
        return token
    }

    private fun findToken(): Token {
        if (currentChar.isLetter()) {
            val literal = readIdentifier()
            val tokenType = Token.lookupIdentifier(literal)
            return Token(tokenType, literal)
        }

        return if (currentChar.isDigit())
            Token(TokenType.INTEGER, readNumber())
        else
            Token(TokenType.ILLEGAL, currentChar.toString())
    }

    private fun readIdentifier(): String {
        val startPosition = position
        while (currentChar.isLetter())
            readChar()
        return input.substring(startPosition, position)
    }

    private fun readNumber(): String {
        val startPosition = position
        while (currentChar.isDigit())
            readChar()
        return input.substring(startPosition, position)
    }

    // TODO: Add support for escaped strings "\t\n\s"
    private fun readString(): String {
        val startPosition = position + 1
        do {
            readChar()
        } while (currentChar != '"' && currentChar != '\u0000')
        return input.substring(startPosition, position)
    }

    private fun skipWhitespace() {
        while (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r')
            readChar()
    }

    override fun toString(): String {
        return "Lexer(input=$input, position=$position, readPosition=$readPosition, currentChar=$currentChar)"
    }
}