package dev.mvmo.sicklang.token

data class Token(val type: TokenType, val literal: String) {

    companion object {
        private val KEYWORDS_TYPE_MAP = mapOf(
            "fn" to TokenType.FUNCTION,
            "let" to TokenType.LET,
            "true" to TokenType.TRUE,
            "false" to TokenType.FALSE,
            "if" to TokenType.IF,
            "else" to TokenType.ELSE,
            "return" to TokenType.RETURN
        )

        fun lookupIdentifier(identifier: String): TokenType {
            return KEYWORDS_TYPE_MAP.getOrDefault(identifier, TokenType.IDENTIFIER)
        }
    }

}