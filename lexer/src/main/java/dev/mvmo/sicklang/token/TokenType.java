package dev.mvmo.sicklang.token;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenType {

    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    IDENTIFIER("IDENT"),
    INTEGER("INT"),

    // Operators
    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    BANG("!"),
    ASTERISK("*"),
    SLASH("/"),

    LESS_THAN("<"),
    GREATER_THAN(">"),

    COMMA(","),
    SEMICOLON(";"),

    LEFT_PAREN("("),
    RIGHT_PAREN(")"),

    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),

    // Keywords
    FUNCTION("FUNCTION"),
    LET("LET"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    IF("IF"),
    ELSE("ELSE"),
    RETURN("RETURN");

    private final String literal;

}
