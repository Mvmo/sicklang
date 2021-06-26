package dev.mvmo.sicklang;

import dev.mvmo.sicklang.token.Token;
import dev.mvmo.sicklang.token.TokenType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Lexer {

    private final String input;

    private int position; // current position on input (points to current char)
    private int readPosition; // current reading position in input (after current char)
    private char currentChar; // current char under examination

    public static Lexer newInstance(String input) {
        var lexer = new Lexer(input);
        lexer.readChar();

        return lexer;
    }

    private void readChar() {
        if (this.readPosition >= this.input.length())
            this.currentChar = 0;
        else
            this.currentChar = this.input.charAt(this.readPosition);

        this.position = this.readPosition;
        this.readPosition += 1;
    }

    private char peekChar() {
        if (this.readPosition >= this.input.length())
            return 0;
        else
            return this.input.charAt(this.readPosition);
    }

    public Token nextToken() {
        skipWhitespace();

        Token token;

        switch (currentChar) {
            //<editor-fold desc="operators">
            case '=':
                if (peekChar() == '=') {
                    char c = currentChar;
                    readChar();

                    var literal = "" + c + currentChar;

                    token = new Token(TokenType.EQUALS, literal);
                    break;
                }

                token = new Token(TokenType.ASSIGN, "=");
                break;
            case '+':
                token = new Token(TokenType.PLUS, "+");
                break;
            case '-':
                token = new Token(TokenType.MINUS, "-");
                break;
            case '*':
                token = new Token(TokenType.ASTERISK, "*");
                break;
            case '/':
                token = new Token(TokenType.SLASH, "/");
                break;
            case '!':
                if (peekChar() == '=') {
                    char c = currentChar;
                    readChar();

                    var literal = "" + c + currentChar;

                    token = new Token(TokenType.NOT_EQUALS, literal);
                    break;
                }
                token = new Token(TokenType.BANG, "!");
                break;
            case '<':
                token = new Token(TokenType.LESS_THAN, "<");
                break;
            case '>':
                token = new Token(TokenType.GREATER_THAN, ">");
                break;
            //</editor-fold>
            case ';':
                token = new Token(TokenType.SEMICOLON, ";");
                break;
            case '(':
                token = new Token(TokenType.LEFT_PAREN, "(");
                break;
            case ')':
                token = new Token(TokenType.RIGHT_PAREN, ")");
                break;
            case ',':
                token = new Token(TokenType.COMMA, ",");
                break;
            case '{':
                token = new Token(TokenType.LEFT_BRACE, "{");
                break;
            case '}':
                token = new Token(TokenType.RIGHT_BRACE, "}");
                break;
            case 0:
                token = new Token(TokenType.EOF, "");
                break;
            default:
                return findToken();
        }

        readChar();

        return token;
    }

    private Token findToken() {
        if (letter(currentChar)) {
            var literal = readIdentifier();
            var tokenType = Token.lookupIdentifier(literal);

            return new Token(tokenType, literal);
        }

        if (digit(currentChar)) {
            return new Token(TokenType.INTEGER, readNumber());
        }

        return new Token(TokenType.ILLEGAL, String.valueOf(this.currentChar));
    }

    private String readIdentifier() {
        int startPosition = this.position;
        while (letter(currentChar))
            readChar();

        return this.input.substring(startPosition, this.position);
    }

    private String readNumber() {
        int startPosition = this.position;
        while (digit(currentChar))
            readChar();

        return this.input.substring(startPosition, this.position);
    }

    private void skipWhitespace() {
        while (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r')
            readChar();
    }

    private boolean letter(char c) {
        return Character.isLetter(c);
    }

    private boolean digit(char c) {
        return Character.isDigit(c);
    }

}
