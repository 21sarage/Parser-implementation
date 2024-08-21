package lab09_05_11.visitors.execution;

import java.util.ArrayList;
import java.util.Collections;

public class VectorValue extends AtomicValue<ArrayList<Integer>>{

    public VectorValue(Integer ind, Integer dim) {
        super(new ArrayList<>());
        if(dim<0) throw new InterpreterException("Vector has Negative dimension.");
        if(ind < 0 || ind >= dim) throw new InterpreterException("Index '" + ind + "' out of bound.");
        super.value.addAll(Collections.nCopies(dim, 0));
        super.value.set(ind, 1);
    }

    protected VectorValue(ArrayList<Integer> l){
        super(l);
    }

    @Override
    public VectorValue toVector() {
        return this;
    }

    @Override
    public String toString() {
        return super.toString().replace(", ", ";" );
    }
}