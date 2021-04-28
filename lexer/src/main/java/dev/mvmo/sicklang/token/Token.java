package dev.mvmo.sicklang.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@ToString
@Accessors(fluent = true)
@AllArgsConstructor(staticName = "newInstance")
public class Token {

    private static final Map<String, TokenType> KEYWORDS_TYPE_MAP = Map.of(
            "fn", TokenType.FUNCTION,
            "let", TokenType.LET
    );

    public static TokenType lookupIdentifier(String identifier) {
        return KEYWORDS_TYPE_MAP.getOrDefault(identifier, TokenType.IDENTIFIER);
    }

    private TokenType type;
    private String literal;

}
