package lab09_05_11.parser.ast;

import static java.util.Objects.requireNonNull;

import lab09_05_11.visitors.Visitor;

public class ForEach implements Stmt {
    private final Variable variable;
    private final Exp expression;
    private final Block block;

    public ForEach(Variable variable, Exp expression, Block block) {
        this.variable = requireNonNull(variable);
        this.expression = requireNonNull(expression);
        this.block = requireNonNull(block);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variable + "," + expression + "," + block + ")";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitForEach(variable, expression, block);
    }
}
