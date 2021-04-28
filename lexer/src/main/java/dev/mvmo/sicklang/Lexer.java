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
        Lexer lexer = new Lexer(input);
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

                    String literal = "" + c + currentChar;
                    token = Token.newInstance(TokenType.EQUALS, literal);
                    break;
                }

                token = Token.newInstance(TokenType.ASSIGN, "=");
                break;
            case '+':
                token = Token.newInstance(TokenType.PLUS, "+");
                break;
            case '-':
                token = Token.newInstance(TokenType.MINUS, "-");
                break;
            case '*':
                token = Token.newInstance(TokenType.ASTERISK, "*");
                break;
            case '/':
                token = Token.newInstance(TokenType.SLASH, "/");
                break;
            case '!':
                if (peekChar() == '=') {
                    char c = currentChar;
                    readChar();

                    String literal = "" + c + currentChar;
                    token = Token.newInstance(TokenType.NOT_EQUALS, literal);
                    break;
                }
                token = Token.newInstance(TokenType.BANG, "!");
                break;
            case '<':
                token = Token.newInstance(TokenType.LESS_THAN, "<");
                break;
            case '>':
                token = Token.newInstance(TokenType.GREATER_THAN, ">");
                break;
            //</editor-fold>
            case ';':
                token = Token.newInstance(TokenType.SEMICOLON, ";");
                break;
            case '(':
                token = Token.newInstance(TokenType.LEFT_PAREN, "(");
                break;
            case ')':
                token = Token.newInstance(TokenType.RIGHT_PAREN, ")");
                break;
            case ',':
                token = Token.newInstance(TokenType.COMMA, ",");
                break;
            case '{':
                token = Token.newInstance(TokenType.LEFT_BRACE, "{");
                break;
            case '}':
                token = Token.newInstance(TokenType.RIGHT_BRACE, "}");
                break;
            case 0:
                token = Token.newInstance(TokenType.EOF, "");
                break;
            default:
                return findToken();
        }

        readChar();
        return token;
    }

    private Token findToken() {
        if (isLetter(currentChar)) {
            String literal = readIdentifier();
            return Token.newInstance(Token.lookupIdentifier(literal), literal);
        }

        if (isDigit(currentChar)) {
            return Token.newInstance(TokenType.INTEGER, readNumber());
        }

        return Token.newInstance(TokenType.ILLEGAL, String.valueOf(this.currentChar));
    }

    private String readIdentifier() {
        int startPosition = this.position;
        while (isLetter(currentChar))
            readChar();

        return this.input.substring(startPosition, this.position);
    }

    private String readNumber() {
        int startPosition = this.position;
        while (isDigit(currentChar))
            readChar();

        return this.input.substring(startPosition, this.position);
    }

    private void skipWhitespace() {
        while (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r')
            readChar();
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

}
