package dev.mvmo.sicklang.token

enum class TokenType(private val literal: String) {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),
    IDENTIFIER("IDENT"),
    INTEGER("INT"),
    STRING("STRING"),
    // Operators
    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    BANG("!"),
    ASTERISK("*"),
    SLASH("/"),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    EQUALS("=="),
    NOT_EQUALS("!="),
    COMMA(","),
    COLON(":"),
    SEMICOLON(";"),
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]"),
    // Keywords
    FUNCTION("FUNCTION"),
    LET("LET"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    IF("IF"),
    ELSE("ELSE"),
    RETURN("RETURN")

}