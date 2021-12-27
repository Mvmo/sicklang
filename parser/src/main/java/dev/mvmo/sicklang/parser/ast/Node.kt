package dev.mvmo.sicklang.parser.ast

interface Node {
    fun tokenLiteral(): String

    override fun toString(): String
}