package lab09_05_11.visitors.execution;

import java.io.PrintWriter;
import java.util.ArrayList;
import lab09_05_11.environments.EnvironmentException;
import lab09_05_11.environments.GenEnvironment;
import lab09_05_11.visitors.Visitor;
import lab09_05_11.parser.ast.*;

import static java.util.Objects.requireNonNull;

public class Execute implements Visitor<Value> {

	private final GenEnvironment<Value> env = new GenEnvironment<>();
	private final PrintWriter printWriter; // output stream used to print values

	public Execute() {
		printWriter = new PrintWriter(System.out, true);
	}

	public Execute(PrintWriter printWriter) {
		this.printWriter = requireNonNull(printWriter);
	}

	// dynamic semantics for programs; no value returned by the visitor

	@Override
	public Value visitMyLangProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
			// possible runtime errors
			// EnvironmentException: undefined variable
		} catch (EnvironmentException e) {
			throw new InterpreterException(e);
		}
		return null;
	}

	// dynamic semantics for statements; no value returned by the visitor

	@Override
	public Value visitAssignStmt(Variable var, Exp exp) {
		env.update(var, exp.accept(this));
		return null;
	}

	@Override
	public Value visitPrintStmt(Exp exp) {
		printWriter.println(exp.accept(this));
		return null;
	}

	@Override
	public Value visitVarStmt(Variable var, Exp exp) {
		env.dec(var, exp.accept(this));
		return null;
	}

	@Override
	public Value visitIfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		if (exp.accept(this).toBool())
			thenBlock.accept(this);
		else if (elseBlock != null)
			elseBlock.accept(this);
		return null;
	}

	@Override
	public Value visitForEach(Variable variable, Exp expression, Block block){
		var vector = expression.accept(this).toVector();
		env.enterScope();
		env.dec(variable, new IntValue(0));
		for(int i = 0; i < vector.value.size(); i++) {
			env.update(variable, new IntValue(vector.value.get(i)));
			block.accept(this);
		}
		env.exitScope();
		return null;
	}

	@Override
	public Value visitBlock(StmtSeq stmtSeq) {
		env.enterScope();
		stmtSeq.accept(this);
		env.exitScope();
		return null;
	}

	// dynamic semantics for sequences of statements
	// no value returned by the visitor

	@Override
	public Value visitEmptyStmtSeq() {
		return null;
	}

	@Override
	public Value visitNonEmptyStmtSeq(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	}

	// dynamic semantics of expressions; a value is returned by the visitor

	@Override
	public Value visitAdd(Exp left, Exp right) {
		var first = left.accept(this);
		var second = right.accept(this);

		if(first instanceof VectorValue && second instanceof VectorValue) {
			if (first.toVector().value.size() != second.toVector().value.size())
				throw new InterpreterException("Vectors must have same dimension.");
			ArrayList<Integer> res = new ArrayList<>();
			for (int i = 0; i < first.toVector().value.size(); i++)
				res.add(first.toVector().value.get(i) + second.toVector().value.get(i));
			return new VectorValue(res);
		}
		else if(first instanceof IntValue && second instanceof IntValue)
			return new IntValue(first.toInt() + second.toInt());
		else if(first instanceof IntValue)
			throw new InterpreterException("Expecting the Dynamic Type Int");
		else if(first instanceof VectorValue)
			throw new InterpreterException("Expecting the Dynamic Type Vector");
		throw new InterpreterException("Expecting Int or Vector Type");
	}

	@Override
	public IntValue visitIntLiteral(int value) {
		return new IntValue(value);
	}

	@Override
	public Value visitMul(Exp left, Exp right) {
		var first = left.accept(this);
		var second = right.accept(this);

		if(first instanceof IntValue && second instanceof IntValue)
			return new IntValue(first.toInt() * second.toInt());
		else if(first instanceof VectorValue && second instanceof VectorValue) {
			if (first.toVector().value.size() != second.toVector().value.size())
				throw new InterpreterException("Vectors must have the same dimension");
			int res = 0;
			for (int i = 0; i < first.toVector().value.size(); i++)
				res += (first.toVector().value.get(i) * second.toVector().value.get(i));
			return new IntValue(res);
		}
		else if ((first instanceof IntValue && second instanceof VectorValue) || (first instanceof VectorValue && second instanceof IntValue)) {
			ArrayList<Integer> res = new ArrayList<>();
			if (first instanceof IntValue) {
				for (int i = 0; i < second.toVector().value.size(); i++)
					res.add(first.toInt() * second.toVector().value.get(i));
			} else {
				for (int i = 0; i < first.toVector().value.size(); i++)
					res.add(second.toInt() * first.toVector().value.get(i));
			}
			return new VectorValue(res);
		}

		throw new InterpreterException("Expecting Int or Vector Type");
	}

	@Override
	public IntValue visitSign(Exp exp) {
		return new IntValue(-exp.accept(this).toInt());
	}

	@Override
	public Value visitVariable(Variable var) {
		return env.lookup(var);
	}

	@Override
	public BoolValue visitNot(Exp exp) {
		return new BoolValue(!exp.accept(this).toBool());
	}

	@Override
	public BoolValue visitAnd(Exp left, Exp right) {
		return new BoolValue(left.accept(this).toBool() && right.accept(this).toBool());
	}

	@Override
	public BoolValue visitBoolLiteral(boolean value) {
		return new BoolValue(value);
	}

	@Override
	public BoolValue visitEq(Exp left, Exp right) {
		return new BoolValue(left.accept(this).equals(right.accept(this)));
	}

	@Override
	public PairValue visitPairLit(Exp left, Exp right) {
		return new PairValue(left.accept(this), right.accept(this));
	}

	@Override
	public Value visitFst(Exp exp) {
		return exp.accept(this).toPair().getFstVal();
	}

	@Override
	public Value visitSnd(Exp exp) {
		return exp.accept(this).toPair().getSndVal();
	}

	@Override
	public Value visitVector(Exp left, Exp right){
		return new VectorValue(left.accept(this).toInt(), right.accept(this).toInt());
	}

}
