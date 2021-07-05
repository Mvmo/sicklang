package dev.mvmo.sicklang.token;

import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

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

    private static final Set<String> TYPES = Sets.newHashSet(
            "string",
            "int",
            "array",
            "hash",
            "function"
    );

    public static TokenType lookupIdentifier(String identifier) {
        return KEYWORDS_TYPE_MAP.getOrDefault(identifier, lookupType(identifier));
    }

    public static TokenType lookupType(String identifier) {
        return TYPES.contains(identifier) ? TokenType.TYPE : TokenType.IDENTIFIER;
    }

}
