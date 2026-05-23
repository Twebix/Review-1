package compiler;

public enum TokenType {
    // Literals
    NUMBER,         // integer like 5, 100
    FLOAT_NUMBER,   // float like 3.14
    IDENTIFIER,     // variable names in Bangla e.g. ক, ফলাফল
    STRING,         // "hello"

    // Keywords
    DHORO,      // ধরো  - variable declaration
    SHONGKHA,   // সংখ্যা - int type
    DOSHOMIK,   // দশমিক - float type
    JODI,       // যদি   - if
    NAHOLE,     // নাহলে - else
    JOTOKHON,   // যতক্ষণ - while
    LIKHO,      // লিখো  - print
    SHOTTO,     // সত্য  - true
    MITHYA,     // মিথ্যা - false

    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
    ASSIGN,         // =
    EQUAL,          // ==
    NOT_EQUAL,      // !=
    LESS,           // <
    LESS_EQUAL,     // <=
    GREATER,        // >
    GREATER_EQUAL,  // >=
    AND,            // &&
    OR,             // ||
    NOT,            // !

    // Delimiters
    LPAREN, RPAREN,   // ( )
    LBRACE, RBRACE,   // { }
    SEMICOLON,        // ;
    COMMA,            // ,

    // Special
    EOF,
    UNKNOWN
}
