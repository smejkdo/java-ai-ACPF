package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TreeIterator<A> implements Iterator<A> {
    private TreeNode<A> iter;
    private boolean started = false;

    public TreeIterator(TreeNode<A> iter) {
        this.iter = iter;
        nextLeft(); //moves iterator most left
    }

    @Override
    public boolean hasNext() {
        if (!started) {
            return iter != null;
        }
        TreeNode<A> tmp = iter;
        if (tmp.hasRight()) {
            return true;
        }
        while (tmp.hasParent()) {
            if (tmp.getParent().hasLeft()
                    && tmp.getParent().getLeft().equals(tmp)) {
                return true;
            } else
                tmp = tmp.getParent();
        }
        return false;
    }

    @Override
    public A next() {
        if (!started) {
            started = true;
            if (iter != null)
                return iter.getA();
            else
                return null;
        }
        if (iter.hasRight()) {
            iter = iter.getRight();
            return nextLeft();
        }
        while (iter.hasParent()) {
            if (iter.getParent().hasLeft()
                    && iter.getParent().getLeft().equals(iter)) {
                iter = iter.getParent();
                return iter.getA();
            } else
                iter = iter.getParent();
        }
        throw new NoSuchElementException();
    }

    private A nextLeft() {
        while (iter.hasLeft()) {
            iter = iter.getLeft();
        }
        return iter.getA();
    }
}
