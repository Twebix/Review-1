package compiler;

import java.util.List;

/** Base class for every node in the Abstract Syntax Tree. */
public abstract class ASTNode {
    public int line;
}

// ════════════════════════════════════════════════════════════
//  Statements
// ════════════════════════════════════════════════════════════

class ProgramNode extends ASTNode {
    public List<ASTNode> statements;
    public ProgramNode(List<ASTNode> statements, int line) {
        this.statements = statements;
        this.line = line;
    }
}

/** ধরো সংখ্যা ক = ৫; */
class VarDeclNode extends ASTNode {
    public String  type;         // "সংখ্যা" or "দশমিক"
    public String  name;
    public ASTNode initializer;  // may be null
    public VarDeclNode(String type, String name, ASTNode initializer, int line) {
        this.type        = type;
        this.name        = name;
        this.initializer = initializer;
        this.line        = line;
    }
}

/** ক = ১০; */
class AssignNode extends ASTNode {
    public String  name;
    public ASTNode value;
    public AssignNode(String name, ASTNode value, int line) {
        this.name  = name;
        this.value = value;
        this.line  = line;
    }
}

/** যদি (শর্ত) { ... } নাহলে { ... } */
class IfNode extends ASTNode {
    public ASTNode       condition;
    public List<ASTNode> thenBranch;
    public List<ASTNode> elseBranch; // may be null
    public IfNode(ASTNode condition, List<ASTNode> thenBranch, List<ASTNode> elseBranch, int line) {
        this.condition  = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.line       = line;
    }
}

/** যতক্ষণ (শর্ত) { ... } */
class WhileNode extends ASTNode {
    public ASTNode       condition;
    public List<ASTNode> body;
    public WhileNode(ASTNode condition, List<ASTNode> body, int line) {
        this.condition = condition;
        this.body      = body;
        this.line      = line;
    }
}

/** লিখো(রাশি); */
class PrintNode extends ASTNode {
    public ASTNode expression;
    public PrintNode(ASTNode expression, int line) {
        this.expression = expression;
        this.line       = line;
    }
}

// ════════════════════════════════════════════════════════════
//  Expressions
// ════════════════════════════════════════════════════════════

class BinaryOpNode extends ASTNode {
    public String  op;
    public ASTNode left, right;
    public BinaryOpNode(String op, ASTNode left, ASTNode right, int line) {
        this.op    = op;
        this.left  = left;
        this.right = right;
        this.line  = line;
    }
}

class UnaryOpNode extends ASTNode {
    public String  op;
    public ASTNode operand;
    public UnaryOpNode(String op, ASTNode operand, int line) {
        this.op      = op;
        this.operand = operand;
        this.line    = line;
    }
}

class NumberNode extends ASTNode {
    public int value;
    public NumberNode(int value, int line) {
        this.value = value;
        this.line  = line;
    }
}

class FloatNode extends ASTNode {
    public double value;
    public FloatNode(double value, int line) {
        this.value = value;
        this.line  = line;
    }
}

class StringNode extends ASTNode {
    public String value;
    public StringNode(String value, int line) {
        this.value = value;
        this.line  = line;
    }
}

class BoolNode extends ASTNode {
    public boolean value;
    public BoolNode(boolean value, int line) {
        this.value = value;
        this.line  = line;
    }
}

class IdentifierNode extends ASTNode {
    public String name;
    public IdentifierNode(String name, int line) {
        this.name = name;
        this.line = line;
    }
}
