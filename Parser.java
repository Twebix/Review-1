import java.util.*;

public class Parser {

    ArrayList<Token> tokens;
    int index = 0;
    SymbolTable sym;

    public Parser(ArrayList<Token> tokens, SymbolTable sym) {
        this.tokens = tokens;
        this.sym = sym;
    }

    public Token peek() {
        if (index < tokens.size()) return tokens.get(index);
        return null;
    }

    public Token consume() {
        return tokens.get(index++);
    }

    public void parse() {
        while (peek() != null) {
            statement();
        }
    }

    void statement() {
        Token t = peek();

        if (t.type.equals("KEYWORD")) {
            consume(); // ধরি
            String var = consume().value;
            consume(); // =
            int val = expr();
            sym.set(var, val);

            if (peek() != null && peek().type.equals("SEMI"))
                consume();

        } else if (t.type.equals("ID")) {
            String var = consume().value;
            consume(); // =
            int val = expr();
            sym.set(var, val);

            if (peek() != null && peek().type.equals("SEMI"))
                consume();
        }
    }

    int expr() {
        int result = term();

        while (peek() != null &&
              (peek().type.equals("PLUS") || peek().type.equals("MINUS"))) {

            String op = consume().type;
            int right = term();

            if (op.equals("PLUS")) result += right;
            else result -= right;
        }

        return result;
    }

    int term() {
        int result = factor();

        while (peek() != null &&
              (peek().type.equals("MUL") || peek().type.equals("DIV"))) {

            String op = consume().type;
            int right = factor();

            if (op.equals("MUL")) result *= right;
            else result /= right;
        }

        return result;
    }

    int factor() {
        Token t = consume();

        if (t.type.equals("NUMBER")) {
            return Integer.parseInt(t.value);
        }

        if (t.type.equals("ID")) {
            return sym.get(t.value);
        }

        return 0;
    }
}