package dev.mvmo.sicklang.test;

import dev.mvmo.sicklang.Lexer;
import dev.mvmo.sicklang.token.Token;
import dev.mvmo.sicklang.token.TokenType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LexerTest {

    @Test
    public void test$nextToken() {
        String input = "let five = 5;" +
                " let ten = 10;" +
                " let add = fn(x, y) {" +
                "    x + y;" +
                " };" +
                " let result = add(five , ten);" +
                " !-/*5;" +
                " 5 < 10 > 5;" +
                " if (5 < 10) {" +
                "    return true;" +
                " } else {" +
                "    return false;" +
                " }";

        Token[] expectedTokens = new Token[]{
                Token.newInstance(TokenType.LET, "let"),
                Token.newInstance(TokenType.IDENTIFIER, "five"),
                Token.newInstance(TokenType.ASSIGN, "="),
                Token.newInstance(TokenType.INTEGER, "5"),
                Token.newInstance(TokenType.SEMICOLON, ";"),
                Token.newInstance(TokenType.LET, "let"),
                Token.newInstance(TokenType.IDENTIFIER, "ten"),
                Token.newInstance(TokenType.ASSIGN, "="),
                Token.newInstance(TokenType.INTEGER, "10"),
                Token.newInstance(TokenType.SEMICOLON, ";"),
                Token.newInstance(TokenType.LET, "let"),
                Token.newInstance(TokenType.IDENTIFIER, "add"),
                Token.newInstance(TokenType.ASSIGN, "="),
                Token.newInstance(TokenType.FUNCTION, "fn"),
                Token.newInstance(TokenType.LEFT_PAREN, "("),
                Token.newInstance(TokenType.IDENTIFIER, "x"),
                Token.newInstance(TokenType.COMMA, ","),
                Token.newInstance(TokenType.IDENTIFIER, "y"),
                Token.newInstance(TokenType.RIGHT_PAREN, ")"),
                Token.newInstance(TokenType.LEFT_BRACE, "{"),
                Token.newInstance(TokenType.IDENTIFIER, "x"),
                Token.newInstance(TokenType.PLUS, "+"),
                Token.newInstance(TokenType.IDENTIFIER, "y"),
                Token.newInstance(TokenType.SEMICOLON, ";"),
                Token.newInstance(TokenType.RIGHT_BRACE, "}"),
                Token.newInstance(TokenType.SEMICOLON, ";"),
                Token.newInstance(TokenType.LET, "let"),
                Token.newInstance(TokenType.IDENTIFIER, "result"),
                Token.newInstance(TokenType.ASSIGN, "="),
                Token.newInstance(TokenType.IDENTIFIER, "add"),
                Token.newInstance(TokenType.LEFT_PAREN, "("),
                Token.newInstance(TokenType.IDENTIFIER, "five"),
                Token.newInstance(TokenType.COMMA, ","),
                Token.newInstance(TokenType.IDENTIFIER, "ten"),
                Token.newInstance(TokenType.RIGHT_PAREN, ")"),
                Token.newInstance(TokenType.SEMICOLON, ";"),
                Token.newInstance(TokenType.BANG, "!"),
                Token.newInstance(TokenType.MINUS, "-"),
                Token.newInstance(TokenType.SLASH, "/"),
                Token.newInstance(TokenType.ASTERISK, "*"),
                Token.newInstance(TokenType.INTEGER, "5"),
                Token.newInstance(TokenType.SEMICOLON, ";"),
                Token.newInstance(TokenType.INTEGER, "5"),
                Token.newInstance(TokenType.LESS_THAN, "<"),
                Token.newInstance(TokenType.INTEGER, "10"),
                Token.newInstance(TokenType.GREATER_THAN, ">"),
                Token.newInstance(TokenType.INTEGER, "5"),
                Token.newInstance(TokenType.SEMICOLON, ";"),
                Token.newInstance(TokenType.IF, "if"),
                Token.newInstance(TokenType.LEFT_PAREN, "("),
                Token.newInstance(TokenType.INTEGER, "5"),
                Token.newInstance(TokenType.LESS_THAN, "<"),
                Token.newInstance(TokenType.INTEGER, "10"),
                Token.newInstance(TokenType.RIGHT_PAREN, ")"),
                Token.newInstance(TokenType.LEFT_BRACE, "{"),
                Token.newInstance(TokenType.RETURN, "return"),
                Token.newInstance(TokenType.TRUE, "true"),
                Token.newInstance(TokenType.SEMICOLON, ";"),
                Token.newInstance(TokenType.RIGHT_BRACE, "}"),
                Token.newInstance(TokenType.ELSE, "else"),
                Token.newInstance(TokenType.LEFT_BRACE, "{"),
                Token.newInstance(TokenType.RETURN, "return"),
                Token.newInstance(TokenType.FALSE, "false"),
                Token.newInstance(TokenType.SEMICOLON, ";"),
                Token.newInstance(TokenType.RIGHT_BRACE, "}"),
                Token.newInstance(TokenType.EOF, "")
        };

        Lexer lexer = Lexer.newInstance(input);

        for (int i = 0; i < expectedTokens.length; i++) {
            Token expectedToken = expectedTokens[i];
            Token token = lexer.nextToken();

            System.out.println(token.toString());

            assertEquals(String.format("expected[%d] -> Type isn't the same type as the found one.\nExpected ('%s') and found ('%s')" +
                            lexer.toString(), i, expectedToken.type(), token.type()),
                    expectedToken.type(), token.type());

            assertEquals(String.format("expected[%d] -> Literal isn't the same as the found one.\nExpected ('%s') and found ('%s')", i, expectedToken.literal(), token.literal()),
                    token.literal(), expectedToken.literal());
        }
    }

}
