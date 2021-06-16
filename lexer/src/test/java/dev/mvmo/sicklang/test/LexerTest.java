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
                " }" +
                "10 == 10;" +
                "10 != 9;";

        Token[] expectedTokens = new Token[]{
                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENTIFIER, "five"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.INTEGER, "5"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENTIFIER, "ten"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.INTEGER, "10"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENTIFIER, "add"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.FUNCTION, "fn"),
                new Token(TokenType.LEFT_PAREN, "("),
                new Token(TokenType.IDENTIFIER, "x"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.IDENTIFIER, "y"),
                new Token(TokenType.RIGHT_PAREN, ")"),
                new Token(TokenType.LEFT_BRACE, "{"),
                new Token(TokenType.IDENTIFIER, "x"),
                new Token(TokenType.PLUS, "+"),
                new Token(TokenType.IDENTIFIER, "y"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.RIGHT_BRACE, "}"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENTIFIER, "result"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.IDENTIFIER, "add"),
                new Token(TokenType.LEFT_PAREN, "("),
                new Token(TokenType.IDENTIFIER, "five"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.IDENTIFIER, "ten"),
                new Token(TokenType.RIGHT_PAREN, ")"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.BANG, "!"),
                new Token(TokenType.MINUS, "-"),
                new Token(TokenType.SLASH, "/"),
                new Token(TokenType.ASTERISK, "*"),
                new Token(TokenType.INTEGER, "5"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.INTEGER, "5"),
                new Token(TokenType.LESS_THAN, "<"),
                new Token(TokenType.INTEGER, "10"),
                new Token(TokenType.GREATER_THAN, ">"),
                new Token(TokenType.INTEGER, "5"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.IF, "if"),
                new Token(TokenType.LEFT_PAREN, "("),
                new Token(TokenType.INTEGER, "5"),
                new Token(TokenType.LESS_THAN, "<"),
                new Token(TokenType.INTEGER, "10"),
                new Token(TokenType.RIGHT_PAREN, ")"),
                new Token(TokenType.LEFT_BRACE, "{"),
                new Token(TokenType.RETURN, "return"),
                new Token(TokenType.TRUE, "true"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.RIGHT_BRACE, "}"),
                new Token(TokenType.ELSE, "else"),
                new Token(TokenType.LEFT_BRACE, "{"),
                new Token(TokenType.RETURN, "return"),
                new Token(TokenType.FALSE, "false"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.RIGHT_BRACE, "}"),
                new Token(TokenType.INTEGER, "10"),
                new Token(TokenType.EQUALS, "=="),
                new Token(TokenType.INTEGER, "10"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.INTEGER, "10"),
                new Token(TokenType.NOT_EQUALS, "!="),
                new Token(TokenType.INTEGER, "9"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.EOF, "")
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
