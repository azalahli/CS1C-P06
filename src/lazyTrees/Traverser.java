package lazyTrees;

/**
 * Created by Mober6 on 5/17/2017.
 * Traverser and printObject
 * Final Version
 * @author Myron Pow 5/17/2017
 */

public interface Traverser<E> {
    public void visit(E x);
}

class PrintObject<E> implements Traverser<E> {
    /**
     * Prints out data from node object
     * @param x xused for looking at and printing data from nodes
     */
    public void visit(E x) {
        System.out.print(x + " ");
    }
};