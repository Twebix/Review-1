import java.util.*;

public class Lexer {

    public static ArrayList<Token> tokenize(String input) {
        ArrayList<Token> tokens = new ArrayList<>();

        String[] words = input.replace(";", " ;")
                              .replace("=", " = ")
                              .replace("+", " + ")
                              .replace("-", " - ")
                              .replace("*", " * ")
                              .replace("/", " / ")
                              .split("\\s+");

        for (String w : words) {

            if (w.equals("ধরি")) tokens.add(new Token("KEYWORD", w));
            else if (w.equals("দেখাও")) tokens.add(new Token("PRINT", w));
            else if (w.equals("+")) tokens.add(new Token("PLUS", w));
            else if (w.equals("-")) tokens.add(new Token("MINUS", w));
            else if (w.equals("*")) tokens.add(new Token("MUL", w));
            else if (w.equals("/")) tokens.add(new Token("DIV", w));
            else if (w.equals("=")) tokens.add(new Token("ASSIGN", w));
            else if (w.equals(";")) tokens.add(new Token("SEMI", w));
            else if (w.matches("[0-9]+")) tokens.add(new Token("NUMBER", w));
            else if (w.matches("[a-zA-Z]+")) tokens.add(new Token("ID", w));
        }

        return tokens;
    }
}