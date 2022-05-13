package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class TreeBFSIterator<A> implements Iterator<A> {
    private LinkedList<TreeNode<A>> opened = new LinkedList<>();


    public TreeBFSIterator(TreeNode<A> root) {
        opened.addLast(root);
    }

    public TreeNode<A> peekNextNode() {
        return opened.element();
    }


    @Override
    public boolean hasNext() {
        return !opened.isEmpty();
    }

    @Override
    public A next() {
        if (!hasNext())
            throw new NoSuchElementException();
        TreeNode<A> next = opened.removeFirst();
        if (next.hasLeft())
            opened.addLast(next.getLeft());
        if (next.hasRight())
            opened.addLast(next.getRight());

        return next.getA();
    }
}
