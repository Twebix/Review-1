package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    private final String source;
    private int pos;
    private int line;
    private final List<Token>  tokens = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    // Bengali digit map: ০→0 ... ৯→9
    private static final String BENGALI_DIGITS = "০১২৩৪৫৬৭৮৯";

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    static {
        KEYWORDS.put("ধরো",    TokenType.DHORO);
        KEYWORDS.put("সংখ্যা", TokenType.SHONGKHA);
        KEYWORDS.put("দশমিক",  TokenType.DOSHOMIK);
        KEYWORDS.put("যদি",    TokenType.JODI);
        KEYWORDS.put("নাহলে",  TokenType.NAHOLE);
        KEYWORDS.put("যতক্ষণ", TokenType.JOTOKHON);
        KEYWORDS.put("লিখো",   TokenType.LIKHO);
        KEYWORDS.put("সত্য",   TokenType.SHOTTO);
        KEYWORDS.put("মিথ্যা", TokenType.MITHYA);
    }

    public Lexer(String source) {
        this.source = source;
        this.pos    = 0;
        this.line   = 1;
    }

    public List<Token> tokenize() {
        while (pos < source.length()) {
            skipWhitespaceAndComments();
            if (pos >= source.length()) break;

            char c = source.charAt(pos);

            if (isDigitChar(c)) {
                readNumber();
            } else if (isIdentStart(c)) {
                readIdentifierOrKeyword();
            } else if (c == '"') {
                readString();
            } else {
                readSymbol();
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    // ── Character helpers ────────────────────────────────────

    private boolean isDigitChar(char c) {
        return Character.isDigit(c) || BENGALI_DIGITS.indexOf(c) >= 0;
    }

    private boolean isIdentStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isIdentPart(char c) {
        return Character.isLetterOrDigit(c)
            || c == '_'
            || BENGALI_DIGITS.indexOf(c) >= 0
            || Character.getType(c) == Character.NON_SPACING_MARK
            || Character.getType(c) == Character.COMBINING_SPACING_MARK;
    }

    /** Convert Bengali/ASCII digit character to ASCII digit char */
    private char toAsciiDigit(char c) {
        int idx = BENGALI_DIGITS.indexOf(c);
        if (idx >= 0) return (char) ('0' + idx);
        return c;
    }

    // ── Skip whitespace & comments ───────────────────────────

    private void skipWhitespaceAndComments() {
        while (pos < source.length()) {
            char c = source.charAt(pos);
            if (c == '\n') {
                line++; pos++;
            } else if (Character.isWhitespace(c)) {
                pos++;
            } else if (startsWith("//")) {
                while (pos < source.length() && source.charAt(pos) != '\n') pos++;
            } else if (startsWith("/*")) {
                pos += 2;
                while (pos + 1 < source.length()) {
                    if (source.charAt(pos) == '\n') line++;
                    if (startsWith("*/")) { pos += 2; break; }
                    pos++;
                }
            } else {
                break;
            }
        }
    }

    private boolean startsWith(String s) {
        return source.startsWith(s, pos);
    }

    // ── Token readers ────────────────────────────────────────

    private void readNumber() {
        StringBuilder num = new StringBuilder();
        boolean isFloat = false;

        while (pos < source.length() && isDigitChar(source.charAt(pos))) {
            num.append(toAsciiDigit(source.charAt(pos)));
            pos++;
        }
        if (pos < source.length() && source.charAt(pos) == '.') {
            isFloat = true;
            num.append('.');
            pos++;
            while (pos < source.length() && isDigitChar(source.charAt(pos))) {
                num.append(toAsciiDigit(source.charAt(pos)));
                pos++;
            }
        }

        tokens.add(new Token(isFloat ? TokenType.FLOAT_NUMBER : TokenType.NUMBER,
                             num.toString(), line));
    }

    private void readIdentifierOrKeyword() {
        int start = pos;
        pos++;
        while (pos < source.length() && isIdentPart(source.charAt(pos))) pos++;
        String word = source.substring(start, pos);
        TokenType type = KEYWORDS.getOrDefault(word, TokenType.IDENTIFIER);
        tokens.add(new Token(type, word, line));
    }

    private void readString() {
        pos++; // skip opening "
        StringBuilder sb = new StringBuilder();
        while (pos < source.length() && source.charAt(pos) != '"') {
            char c = source.charAt(pos);
            if (c == '\\' && pos + 1 < source.length()) {
                char esc = source.charAt(pos + 1);
                switch (esc) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    default:  sb.append(esc);
                }
                pos += 2;
            } else {
                if (c == '\n') line++;
                sb.append(c);
                pos++;
            }
        }
        if (pos < source.length()) pos++; // skip closing "
        tokens.add(new Token(TokenType.STRING, sb.toString(), line));
    }

    private void readSymbol() {
        char c    = source.charAt(pos);
        char next = (pos + 1 < source.length()) ? source.charAt(pos + 1) : '\0';

        switch (c) {
            case '+': add(TokenType.PLUS,      "+"); pos++;    break;
            case '-': add(TokenType.MINUS,     "-"); pos++;    break;
            case '*': add(TokenType.MULTIPLY,  "*"); pos++;    break;
            case '/': add(TokenType.DIVIDE,    "/"); pos++;    break;
            case '%': add(TokenType.MODULO,    "%"); pos++;    break;
            case '(': add(TokenType.LPAREN,    "("); pos++;    break;
            case ')': add(TokenType.RPAREN,    ")"); pos++;    break;
            case '{': add(TokenType.LBRACE,    "{"); pos++;    break;
            case '}': add(TokenType.RBRACE,    "}"); pos++;    break;
            case ';': add(TokenType.SEMICOLON, ";"); pos++;    break;
            case ',': add(TokenType.COMMA,     ","); pos++;    break;
            case '=':
                if (next == '=') { add(TokenType.EQUAL,         "=="); pos += 2; }
                else             { add(TokenType.ASSIGN,          "="); pos++;   }
                break;
            case '!':
                if (next == '=') { add(TokenType.NOT_EQUAL,     "!="); pos += 2; }
                else             { add(TokenType.NOT,             "!"); pos++;   }
                break;
            case '<':
                if (next == '=') { add(TokenType.LESS_EQUAL,    "<="); pos += 2; }
                else             { add(TokenType.LESS,            "<"); pos++;   }
                break;
            case '>':
                if (next == '=') { add(TokenType.GREATER_EQUAL, ">="); pos += 2; }
                else             { add(TokenType.GREATER,         ">"); pos++;   }
                break;
            case '&':
                if (next == '&') { add(TokenType.AND, "&&"); pos += 2; }
                else { error("অজানা চিহ্ন '&'"); pos++; }
                break;
            case '|':
                if (next == '|') { add(TokenType.OR, "||"); pos += 2; }
                else { error("অজানা চিহ্ন '|'"); pos++; }
                break;
            default:
                error("অজানা চিহ্ন '" + c + "'");
                add(TokenType.UNKNOWN, String.valueOf(c));
                pos++;
                break;
        }
    }

    private void add(TokenType type, String value) {
        tokens.add(new Token(type, value, line));
    }

    private void error(String msg) {
        String e = "[লেক্সার ত্রুটি] লাইন " + line + ": " + msg;
        errors.add(e);
        System.err.println(e);
    }

    public List<String> getErrors() { return errors; }
}
