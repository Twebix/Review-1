public class Token {
    String type;
    String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return type + "(" + value + ")";
    }
}
