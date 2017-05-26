package lazyTrees;


import java.util.*;

/**
 * Implementation of a Lazy Binary search tree
 * File functions mostly copied from FHsearch_tree
 * Final Version
 * @author Myron Pow 5/17/17
 */
public class LazySearchTree<E extends Comparable< ? super E > >
        implements Cloneable {
    protected int mSize;
    protected int mSizeHard;
    protected LazySTNode mRoot;

    /**
     * Creates empty LazyTree
     */
    public LazySearchTree() {
        clear();
    }

    /**
     * Checks to see if the tree is empty
     * @return boolean value 0 = has nodes 1 = empty
     */
    public boolean empty() {
        return (mSize == 0);
    }

    /**
     * Accessor for soft size
     * @return int of soft nodes
     */
    public int size() {
        return mSize;
    }

    /**
     *Clears tree to default values
     */
    public void clear() {
        mSize = 0;
        mSizeHard = 0;
        mRoot = null;
    }

    public int showHeight() {
        return findHeight(mRoot, -1);
    }

    /**
     * Accessor for LazyTree size, incl "deleted"
     * @return total number of nodes
     */
    public int sizeHard() {
        return mSizeHard;
    }

    /**
     * Finds smallest soft value
     * @return smallest soft datum
     */
    public E findMin() {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMin(mRoot).data;
    }

    /**
     * Finds largest soft value
     * @return largest soft datum
     */
    public E findMax() {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMax(mRoot).data;
    }

    /**
     * Searches tree for a value, returns data from node equal to value
     * @param x value to find in tree
     * @return data from node containing x
     */
    public E find(E x) {
        LazySTNode resultNode;
        resultNode = find(mRoot, x);
        if (resultNode == null)
            throw new NoSuchElementException();
        return resultNode.data;
    }

    /**
     * Find encapsulated into boolean form
     * @param x value to find
     * @return boolean value if value is present in tree
     */
    public boolean contains(E x) {
        return find(mRoot, x) != null;
    }

    /**
     * Inserts node into tree
     * @param x data to be wrapped in node and added to tree
     * @return boolean value based on if any operation is done
     */
    public boolean insert(E x) {
        int oldSize = mSize;
        mRoot = insert(mRoot, x);
        return (mSize != oldSize);
    }

    /**
     * Lazily removes data from tree
     * @param x
     * @return boolean value based on if operation is done
     */
    public boolean remove(E x) {
        int oldSize = mSize;
        remove(mRoot, x);
        return (mSize != oldSize);
    }

    /**
     * Traverses the tree (softly, and thus ignoring "deleted" nodes
     * @param func printObject(s) in this case
     * @param <F> as above
     */
    public <F extends Traverser<? super E>>
    void traverseSoft(F func) {
        traverseSoft(func, mRoot);
    }

    /**
     * Traverses ALL tree nodes, including "deleted"
     * @param func printObjects(s)
     * @param <F> as above
     */
    public <F extends Traverser<? super E>>
    void traverseHard(F func) {
        traverseHard(func, mRoot);
    }

    /**
     * Clones a tree
     * @return cloned object
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        LazySearchTree<E> newObject = (LazySearchTree<E>) super.clone();
        newObject.clear();  // can't point to other's data

        newObject.mRoot = cloneSubtree(mRoot);
        newObject.mSize = mSize;

        return newObject;
    }

    // private helper methods ----------------------------------------

    /**
     * Takes a tree's root and finds the smallest value present
     * @param root tree to search
     * @return node that contains lowest value
     */
    protected LazySTNode findMin(LazySTNode root) {
        if (root == null)
            return null;
        LazySTNode temp = findMin(root.lftChild);
        if (temp != null)
            return temp;
        if (!root.deleted)
            return root;
        return findMin(root.rtChild);
    }

    /**
     * Takes a tree root and finds largest value
     * @param root tree to search
     * @return node that contains highest value
     */
    protected LazySTNode findMax(LazySTNode root) {
        if (root == null)
            return null;
        LazySTNode temp = findMax(root.rtChild);
        if (temp != null)
            return temp;
        if (!root.deleted)
            return root;
        return findMax(root.lftChild);
    }

    /**
     * Insertion function for tree
     * @param root tree to add to
     * @param x data to wrap and add to tree
     * @return
     */
    protected LazySTNode insert(LazySTNode root, E x) {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null) {
            mSize++;
            mSizeHard++;
            return new LazySTNode(x, null, null);
        }

        compareResult = x.compareTo(root.data);
        if (compareResult < 0)
            root.lftChild = insert(root.lftChild, x);
        else if (compareResult > 0)
            root.rtChild = insert(root.rtChild, x);
        else if (root.deleted){
            root.deleted = false;
            mSize++;
        }

        return root;
    }

    /**
     * Lazily removes data from tree
     * @param root tree to search
     * @param x data to delete
     */
    protected void remove(LazySTNode root, E x) {
        if(root == null)
            return;
        LazySTNode temp = find(root, x);
        if (temp != null){
            temp.deleted = true;
            mSize--;
        }
    }

    /**
     * Traverses whole tree, including "deleted"
     * @param func printObject, but any generic function will do
     * @param treeNode tree to parse
     * @param <F> printObject again
     */
    protected <F extends Traverser<? super E>>
    void traverseHard(F func, LazySTNode treeNode) {
        if (treeNode == null)
            return;

        traverseHard(func, treeNode.lftChild);
        func.visit(treeNode.data);
        traverseHard(func, treeNode.rtChild);
    }

    /**
     *Traverses tree, ignoring deleted nodes
     * @param func printObject
     * @param treeNode tree to parse
     * @param <F> printObject again
     */
    protected <F extends Traverser<? super E>>
    void traverseSoft(F func, LazySTNode treeNode) {
        if (treeNode == null)
            return;

        traverseSoft(func, treeNode.lftChild);
        if (!treeNode.deleted)
            func.visit(treeNode.data);
        traverseSoft(func, treeNode.rtChild);
    }

    /**
     * Finds node with data if not lazily deleted
     * @param root tree to search
     * @param x data to find
     * @return node with data
     */
    protected LazySTNode find(LazySTNode root, E x) {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if (compareResult < 0)
            return find(root.lftChild, x);
        if (compareResult > 0)
            return find(root.rtChild, x);
        if (root.deleted)
            return null;
        return root;   // found
    }

    /**
     * Clones a subtree of the tree
     * @param root tree root of subtree
     * @return cloned subtree
     */
    protected LazySTNode cloneSubtree(LazySTNode root) {
        LazySTNode newNode;
        if (root == null)
            return null;

        // does not set myRoot which must be done by caller
        newNode = new LazySTNode
                (
                        root.data,
                        cloneSubtree(root.lftChild),
                        cloneSubtree(root.rtChild)
                );
        return newNode;
    }

    protected int findHeight(LazySTNode treeNode, int height) {
        int leftHeight, rightHeight;
        if (treeNode == null)
            return height;
        height++;
        leftHeight = findHeight(treeNode.lftChild, height);
        rightHeight = findHeight(treeNode.rtChild, height);
        return (leftHeight > rightHeight) ? leftHeight : rightHeight;
    }


    /**
     * LazyTree node class
     */
    private class LazySTNode{
        // use public access so the tree or other classes can access members
        protected LazySTNode lftChild, rtChild;
        protected E data;
        protected LazySTNode myRoot;  // needed to test for certain error
        protected boolean deleted;

        protected LazySTNode(E d, LazySTNode lft, LazySTNode rt) {
            lftChild = lft;
            rtChild = rt;
            data = d;
            deleted = false;
        }

        public LazySTNode() {
            this(null, null, null);
        }

        // function stubs -- for use only with AVL Trees when we extend
        public int getHeight() {
            return 0;
        }

        boolean setHeight(int height) {
            return true;
        }
    }
}
