package dev.mvmo.sicklang.parser

import dev.mvmo.sicklang.token.TokenType

open class SickParseException(message: String) : Exception(message) {
    override fun printStackTrace() {
        System.err.println("Oh you got an parsing error!")
        super.printStackTrace()
    }
}

class UnexpectedTokenException(expected: TokenType, actual: TokenType, kind: String = "peek") :
    SickParseException("Expected $kind token to be $expected and found $actual")

class NoParseFunctionException(token: TokenType, position: String = "prefix") :
    SickParseException("No $position parse function found for $token")

