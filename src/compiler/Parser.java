package compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive-descent parser for বাংলাল্যাং.
 *
 * Grammar (informal):
 *   program     → statement* EOF
 *   statement   → varDecl | assign | ifStmt | whileStmt | printStmt
 *   varDecl     → 'ধরো' type IDENTIFIER ('=' expression)? ';'
 *   assign      → IDENTIFIER '=' expression ';'
 *   ifStmt      → 'যদি' '(' expression ')' '{' block '}' ('নাহলে' '{' block '}')?
 *   whileStmt   → 'যতক্ষণ' '(' expression ')' '{' block '}'
 *   printStmt   → 'লিখো' '(' expression ')' ';'
 *   block       → statement*
 *   expression  → or
 *   or          → and ('||' and)*
 *   and         → equality ('&&' equality)*
 *   equality    → relational (('=='|'!=') relational)*
 *   relational  → addSub (('<'|'<='|'>'|'>=') addSub)*
 *   addSub      → mulDiv (('+'|'-') mulDiv)*
 *   mulDiv      → unary (('*'|'/'|'%') unary)*
 *   unary       → ('!'|'-') unary | primary
 *   primary     → NUMBER | FLOAT | STRING | 'সত্য' | 'মিথ্যা' | IDENTIFIER | '(' expression ')'
 */
public class Parser {

    private final List<Token>  tokens;
    private       int          pos;
    private final List<String> errors = new ArrayList<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos    = 0;
    }

    // ── Helpers ──────────────────────────────────────────────

    private Token current()           { return tokens.get(pos); }
    private Token peek(int offset)    {
        int i = pos + offset;
        return i < tokens.size() ? tokens.get(i) : tokens.get(tokens.size() - 1);
    }
    private boolean check(TokenType t){ return current().type == t; }

    private Token consume() {
        Token t = tokens.get(pos);
        if (t.type != TokenType.EOF) pos++;
        return t;
    }

    private boolean match(TokenType t) {
        if (check(t)) { consume(); return true; }
        return false;
    }

    private Token expect(TokenType t) {
        if (check(t)) return consume();
        error("প্রত্যাশিত ছিল " + tokenName(t) + ", কিন্তু পাওয়া গেল '" + current().value + "'");
        // return synthetic token for error recovery (do NOT advance)
        return new Token(t, "", current().line);
    }

    private void error(String msg) {
        String e = "[পার্সার ত্রুটি] লাইন " + current().line + ": " + msg;
        errors.add(e);
        System.err.println(e);
    }

    /** Skip tokens until ';' or '}' (panic-mode error recovery). */
    private void synchronize() {
        while (!check(TokenType.EOF)
            && !check(TokenType.SEMICOLON)
            && !check(TokenType.RBRACE)) {
            consume();
        }
        if (check(TokenType.SEMICOLON)) consume();
    }

    private String tokenName(TokenType t) {
        switch (t) {
            case SEMICOLON: return "';'";
            case LPAREN:    return "'('";
            case RPAREN:    return "')'";
            case LBRACE:    return "'{'";
            case RBRACE:    return "'}'";
            case ASSIGN:    return "'='";
            case IDENTIFIER:return "পরিচয়পত্র";
            default:        return t.toString();
        }
    }

    // ── Entry point ───────────────────────────────────────────

    public ProgramNode parseProgram() {
        int line = current().line;
        List<ASTNode> stmts = new ArrayList<>();
        while (!check(TokenType.EOF)) {
            try {
                ASTNode s = parseStatement();
                if (s != null) stmts.add(s);
            } catch (RuntimeException e) {
                error(e.getMessage() != null ? e.getMessage() : "অজানা ত্রুটি");
                synchronize();
            }
        }
        return new ProgramNode(stmts, line);
    }

    // ── Statements ────────────────────────────────────────────

    private ASTNode parseStatement() {
        switch (current().type) {
            case DHORO:    return parseVarDecl();
            case JODI:     return parseIf();
            case JOTOKHON: return parseWhile();
            case LIKHO:    return parsePrint();
            case IDENTIFIER:
                if (peek(1).type == TokenType.ASSIGN) return parseAssign();
                // fall through
            default:
                error("অপ্রত্যাশিত টোকেন '" + current().value + "'");
                synchronize();
                return null;
        }
    }

    private VarDeclNode parseVarDecl() {
        int line = current().line;
        expect(TokenType.DHORO);

        String type;
        if      (check(TokenType.SHONGKHA)) { type = "সংখ্যা";  consume(); }
        else if (check(TokenType.DOSHOMIK)) { type = "দশমিক";   consume(); }
        else {
            error("'ধরো'-র পরে প্রকার লিখুন (সংখ্যা/দশমিক)");
            type = "সংখ্যা"; // recovery default
        }

        String name = expect(TokenType.IDENTIFIER).value;

        ASTNode init = null;
        if (match(TokenType.ASSIGN)) {
            init = parseExpression();
        }

        expect(TokenType.SEMICOLON);
        return new VarDeclNode(type, name, init, line);
    }

    private AssignNode parseAssign() {
        int    line = current().line;
        String name = consume().value;   // IDENTIFIER
        expect(TokenType.ASSIGN);
        ASTNode value = parseExpression();
        expect(TokenType.SEMICOLON);
        return new AssignNode(name, value, line);
    }

    private IfNode parseIf() {
        int line = current().line;
        expect(TokenType.JODI);
        expect(TokenType.LPAREN);
        ASTNode cond = parseExpression();
        expect(TokenType.RPAREN);
        expect(TokenType.LBRACE);
        List<ASTNode> thenB = parseBlock();
        expect(TokenType.RBRACE);

        List<ASTNode> elseB = null;
        if (match(TokenType.NAHOLE)) {
            expect(TokenType.LBRACE);
            elseB = parseBlock();
            expect(TokenType.RBRACE);
        }

        return new IfNode(cond, thenB, elseB, line);
    }

    private WhileNode parseWhile() {
        int line = current().line;
        expect(TokenType.JOTOKHON);
        expect(TokenType.LPAREN);
        ASTNode cond = parseExpression();
        expect(TokenType.RPAREN);
        expect(TokenType.LBRACE);
        List<ASTNode> body = parseBlock();
        expect(TokenType.RBRACE);
        return new WhileNode(cond, body, line);
    }

    private PrintNode parsePrint() {
        int line = current().line;
        expect(TokenType.LIKHO);
        expect(TokenType.LPAREN);
        ASTNode expr = parseExpression();
        expect(TokenType.RPAREN);
        expect(TokenType.SEMICOLON);
        return new PrintNode(expr, line);
    }

    private List<ASTNode> parseBlock() {
        List<ASTNode> stmts = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !check(TokenType.EOF)) {
            try {
                ASTNode s = parseStatement();
                if (s != null) stmts.add(s);
            } catch (RuntimeException e) {
                error(e.getMessage() != null ? e.getMessage() : "অজানা ত্রুটি");
                synchronize();
            }
        }
        return stmts;
    }

    // ── Expressions (precedence climbing) ────────────────────

    private ASTNode parseExpression() { return parseOr(); }

    private ASTNode parseOr() {
        ASTNode left = parseAnd();
        while (check(TokenType.OR)) {
            int line = current().line; consume();
            left = new BinaryOpNode("||", left, parseAnd(), line);
        }
        return left;
    }

    private ASTNode parseAnd() {
        ASTNode left = parseEquality();
        while (check(TokenType.AND)) {
            int line = current().line; consume();
            left = new BinaryOpNode("&&", left, parseEquality(), line);
        }
        return left;
    }

    private ASTNode parseEquality() {
        ASTNode left = parseRelational();
        while (check(TokenType.EQUAL) || check(TokenType.NOT_EQUAL)) {
            int line = current().line;
            String op = consume().value;
            left = new BinaryOpNode(op, left, parseRelational(), line);
        }
        return left;
    }

    private ASTNode parseRelational() {
        ASTNode left = parseAddSub();
        while (check(TokenType.LESS) || check(TokenType.LESS_EQUAL)
            || check(TokenType.GREATER) || check(TokenType.GREATER_EQUAL)) {
            int line = current().line;
            String op = consume().value;
            left = new BinaryOpNode(op, left, parseAddSub(), line);
        }
        return left;
    }

    private ASTNode parseAddSub() {
        ASTNode left = parseMulDiv();
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            int line = current().line;
            String op = consume().value;
            left = new BinaryOpNode(op, left, parseMulDiv(), line);
        }
        return left;
    }

    private ASTNode parseMulDiv() {
        ASTNode left = parseUnary();
        while (check(TokenType.MULTIPLY) || check(TokenType.DIVIDE) || check(TokenType.MODULO)) {
            int line = current().line;
            String op = consume().value;
            left = new BinaryOpNode(op, left, parseUnary(), line);
        }
        return left;
    }

    private ASTNode parseUnary() {
        if (check(TokenType.NOT)) {
            int line = current().line; consume();
            return new UnaryOpNode("!", parseUnary(), line);
        }
        if (check(TokenType.MINUS)) {
            int line = current().line; consume();
            return new UnaryOpNode("-", parseUnary(), line);
        }
        return parsePrimary();
    }

    private ASTNode parsePrimary() {
        Token t = current();

        switch (t.type) {
            case NUMBER:
                consume();
                return new NumberNode(Integer.parseInt(t.value), t.line);

            case FLOAT_NUMBER:
                consume();
                return new FloatNode(Double.parseDouble(t.value), t.line);

            case STRING:
                consume();
                return new StringNode(t.value, t.line);

            case SHOTTO:
                consume();
                return new BoolNode(true, t.line);

            case MITHYA:
                consume();
                return new BoolNode(false, t.line);

            case IDENTIFIER:
                consume();
                return new IdentifierNode(t.value, t.line);

            case LPAREN:
                consume();
                ASTNode expr = parseExpression();
                expect(TokenType.RPAREN);
                return expr;

            default:
                error("অপ্রত্যাশিত টোকেন '" + t.value + "' (রাশিতে)");
                consume();
                return new NumberNode(0, t.line); // dummy for recovery
        }
    }

    public List<String> getErrors() { return errors; }
}
