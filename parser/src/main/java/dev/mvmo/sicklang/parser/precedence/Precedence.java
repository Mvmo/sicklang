package dev.mvmo.sicklang.parser.precedence;

import com.google.common.collect.Sets;
import dev.mvmo.sicklang.token.TokenType;

import java.util.Arrays;
import java.util.Set;

public enum Precedence {

    LOWEST(),
    EQUALS(TokenType.EQUALS, TokenType.NOT_EQUALS), // ==
    LESS_GREATER_THAN(TokenType.LESS_THAN, TokenType.GREATER_THAN), // > or <
    SUM(TokenType.PLUS, TokenType.MINUS), // +
    PRODUCT(TokenType.SLASH, TokenType.ASTERISK), // *
    PREFIX, // -x or !x
    CALL; // myFunction(x)

    private final Set<TokenType> appliedToTypes;

    Precedence(TokenType... appliedTo) {
        this.appliedToTypes = Sets.newHashSet(appliedTo);
    }

    public static Precedence findPrecedence(TokenType tokenType) {
        return Arrays.stream(values())
                .filter(precedence -> precedence.appliedToTypes.contains(tokenType))
                .findFirst()
                .orElse(LOWEST);
    }

}
