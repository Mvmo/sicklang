package dev.mvmo.sicklang.parser

open class SickParseException(message: String) : Exception(message) {
    override fun printStackTrace() {
        System.err.println("Oh you got an parsing error!")
        super.printStackTrace()
    }
}

