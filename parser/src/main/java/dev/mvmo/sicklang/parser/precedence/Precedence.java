package dev.mvmo.sicklang.parser.precedence;

public enum Precedence {

    LOWEST,
    EQUALS, // ==
    LESS_GREATER_THAN, // > or <
    SUM, // +
    PRODUCT, // *
    PREFIX, // -x or !x
    CALL // myFunction(x)

}
