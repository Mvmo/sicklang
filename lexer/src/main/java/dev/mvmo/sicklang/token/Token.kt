package dev.mvmo.sicklang.token;

import java.util.Map;

public record Token(TokenType type, String literal) {

    private static final Map<String, TokenType> KEYWORDS_TYPE_MAP = Map.of(
            "fn", TokenType.FUNCTION,
            "let", TokenType.LET,
            "true", TokenType.TRUE,
            "false", TokenType.FALSE,
            "if", TokenType.IF,
            "else", TokenType.ELSE,
            "return", TokenType.RETURN
    );

    public static TokenType lookupIdentifier(String identifier) {
        return KEYWORDS_TYPE_MAP.getOrDefault(identifier, TokenType.IDENTIFIER);
    }

}
