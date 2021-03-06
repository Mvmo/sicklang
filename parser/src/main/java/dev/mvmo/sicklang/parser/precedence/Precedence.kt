package dev.mvmo.sicklang.parser.precedence

import com.google.common.collect.Sets
import dev.mvmo.sicklang.token.TokenType

enum class Precedence(vararg appliedTo: TokenType) {

    LOWEST,
    OR(TokenType.OR),
    AND(TokenType.AND),
    EQUALS(TokenType.EQUALS, TokenType.NOT_EQUALS),  // ==
    LESS_GREATER_THAN(TokenType.LESS_THAN, TokenType.GREATER_THAN),  // > or < and or - maybe rename to boolean arithmetic
    SUM(TokenType.PLUS, TokenType.MINUS),  // +
    BITWISE(TokenType.BITWISE_OR, TokenType.BITWISE_AND),
    PRODUCT(TokenType.SLASH, TokenType.ASTERISK),  // *
    PREFIX,  // -x or !x
    CALL(TokenType.LEFT_PAREN),
    INDEX(TokenType.LEFT_BRACKET);

    private val appliedToTypes: Set<TokenType> = Sets.newHashSet(*appliedTo)

    companion object {
        @JvmStatic // todo remove later
        fun findPrecedence(tokenType: TokenType): Precedence =
            values().firstOrNull { it.appliedToTypes.contains(tokenType) } ?: LOWEST
    }
}
