package dev.mvmo.sicklang.token;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenType {

    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    IDENTIFIER("IDENT"),
    INTEGER("INT"),

    ASSIGN("="),
    PLUS("+"),

    COMMA(","),
    SEMICOLON(";"),

    LEFT_PAREN("("),
    RIGHT_PAREN(")"),

    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),

    FUNCTION("FUNCTION"),
    LET("LET");

    private final String literal;

}
