package compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Walks the AST and:
 *  1. Populates the symbol table with variable declarations.
 *  2. Checks that every variable is declared before use.
 *  3. Enforces basic type compatibility (সংখ্যা vs দশমিক).
 *  4. Returns the inferred type of every expression node.
 */
public class TypeChecker {

    private SymbolTable      scope  = new SymbolTable(null); // global scope
    private final List<String> errors = new ArrayList<>();

    // ── Entry point ───────────────────────────────────────────

    public void check(ProgramNode program) {
        for (ASTNode stmt : program.statements) {
            checkStatement(stmt);
        }
    }

    // ── Statements ────────────────────────────────────────────

    private void checkStatement(ASTNode node) {
        if (node == null) return;

        if (node instanceof VarDeclNode)  checkVarDecl((VarDeclNode) node);
        else if (node instanceof AssignNode)  checkAssign((AssignNode) node);
        else if (node instanceof IfNode)      checkIf((IfNode) node);
        else if (node instanceof WhileNode)   checkWhile((WhileNode) node);
        else if (node instanceof PrintNode)   checkExpr(((PrintNode) node).expression);
    }

    private void checkVarDecl(VarDeclNode node) {
        if (scope.isDefinedLocally(node.name)) {
            error(node.line, "'" + node.name + "' আগেই ঘোষণা করা হয়েছে");
        }
        if (node.initializer != null) {
            String initType = checkExpr(node.initializer);
            // সংখ্যা cannot be assigned a দশমিক value
            if ("সংখ্যা".equals(node.type) && "দশমিক".equals(initType)) {
                error(node.line, "'" + node.name + "' সংখ্যা প্রকার, কিন্তু দশমিক মান দেওয়া হয়েছে");
            }
        }
        scope.define(node.name, node.type);
    }

    private void checkAssign(AssignNode node) {
        if (!scope.isDefined(node.name)) {
            error(node.line, "'" + node.name + "' ঘোষণা করা হয়নি");
            return;
        }
        String varType   = scope.lookup(node.name);
        String exprType  = checkExpr(node.value);
        if ("সংখ্যা".equals(varType) && "দশমিক".equals(exprType)) {
            error(node.line, "'" + node.name + "' সংখ্যা প্রকার, কিন্তু দশমিক মান দেওয়া হয়েছে");
        }
    }

    private void checkIf(IfNode node) {
        checkExpr(node.condition);
        pushScope();
        for (ASTNode s : node.thenBranch) checkStatement(s);
        popScope();
        if (node.elseBranch != null) {
            pushScope();
            for (ASTNode s : node.elseBranch) checkStatement(s);
            popScope();
        }
    }

    private void checkWhile(WhileNode node) {
        checkExpr(node.condition);
        pushScope();
        for (ASTNode s : node.body) checkStatement(s);
        popScope();
    }

    // ── Expressions → returns inferred type string ────────────

    private String checkExpr(ASTNode node) {
        if (node == null) return "অজানা";

        if (node instanceof NumberNode)     return "সংখ্যা";
        if (node instanceof FloatNode)      return "দশমিক";
        if (node instanceof StringNode)     return "স্ট্রিং";
        if (node instanceof BoolNode)       return "বুলিয়ান";

        if (node instanceof IdentifierNode) {
            IdentifierNode id = (IdentifierNode) node;
            if (!scope.isDefined(id.name)) {
                error(id.line, "'" + id.name + "' ঘোষণা করা হয়নি");
                return "অজানা";
            }
            return scope.lookup(id.name);
        }

        if (node instanceof UnaryOpNode) {
            UnaryOpNode u = (UnaryOpNode) node;
            return checkExpr(u.operand);
        }

        if (node instanceof BinaryOpNode) {
            BinaryOpNode b = (BinaryOpNode) node;
            String lt = checkExpr(b.left);
            String rt = checkExpr(b.right);

            // Comparison / logical ops → boolean result
            if (b.op.equals("==") || b.op.equals("!=")
             || b.op.equals("<")  || b.op.equals("<=")
             || b.op.equals(">")  || b.op.equals(">=")
             || b.op.equals("&&") || b.op.equals("||")) {
                return "বুলিয়ান";
            }

            // Arithmetic: if either side is দশমিক → result is দশমিক
            if ("দশমিক".equals(lt) || "দশমিক".equals(rt)) return "দশমিক";
            return "সংখ্যা";
        }

        return "অজানা";
    }

    // ── Scope helpers ─────────────────────────────────────────

    private void pushScope() { scope = new SymbolTable(scope); }
    private void popScope()  { scope = scope.parent != null ? scope.parent : scope; }

    // ── Error helpers ─────────────────────────────────────────

    private void error(int line, String msg) {
        String e = "[টাইপ ত্রুটি] লাইন " + line + ": " + msg;
        errors.add(e);
        System.err.println(e);
    }

    public List<String> getErrors() { return errors; }

    // expose scope for code generator
    public SymbolTable getScope() { return scope; }
}
