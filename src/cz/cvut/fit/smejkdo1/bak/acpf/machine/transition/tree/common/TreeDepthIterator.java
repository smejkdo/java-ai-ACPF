package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TreeDepthIterator<A> implements Iterator<A> {
    private TreeNode<A> iter;
    private boolean started = false;
    private int depth;
    private int actualDepth;

    public TreeDepthIterator(TreeNode<A> root, int depth) {
        this.iter = root;
        this.depth = depth;
        actualDepth = 0;
        nextLeft();
    }

    @Override
    public boolean hasNext() {
        if (!started)
            return iter != null;

        TreeNode<A> tmp = iter;
        int tmpDepth = actualDepth;
        while (tmp.hasParent()) {
            if (tmp.getParent().hasLeft()
                    && tmp.getParent().getLeft().equals(tmp)
                    && tmp.getParent().hasRight()) {
                return hasNextLeft(tmp.getParent().getRight(), tmpDepth);
            } else {
                tmp = tmp.getParent();
                tmpDepth--;
            }
        }
        return false;
    }

    private boolean hasNextLeft(TreeNode<A> tmp, int tmpDepth) {
        while (tmp.hasLeft()
                && depth != tmpDepth) {
            tmp = tmp.getLeft();
            tmpDepth++;
        }
        if (depth == tmpDepth)
            return true;

        while (tmp.hasParent()
                && ((tmp.getParent().hasRight()
                && tmp.getParent().getRight().equals(tmp))
                || !tmp.getParent().hasRight())) {
            tmp = tmp.getParent();
            tmpDepth--;
        }
        if (!tmp.hasParent())
            return false;
        else if (tmp.hasRight()) {
            return hasNextLeft(tmp.getRight(), tmpDepth + 1);
        }

        return false;
    }

    public TreeNode<A> getIter() {
        return iter;
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
        while (iter.hasParent()) {
            if (iter.getParent().hasLeft()
                    && iter.getParent().getLeft().equals(iter)
                    && iter.getParent().hasRight()) {
                iter = iter.getParent().getRight();
                return nextLeft();
            } else {
                iter = iter.getParent();
                actualDepth--;
            }
        }
        throw new NoSuchElementException();
    }

    private A nextLeft() {
        while (iter.hasLeft()
                && depth != actualDepth) {
            iter = iter.getLeft();
            actualDepth++;
        }
        if (depth == actualDepth)
            return iter.getA();

        while (iter.hasParent()
                && ((iter.getParent().hasRight()
                && iter.getParent().getRight().equals(iter))
                || !iter.getParent().hasRight())) {
            iter = iter.getParent();
            actualDepth--;
        }
        if (!iter.hasParent())
            throw new NoSuchElementException();
        else if (iter.hasRight()) {
            return nextLeft();
        }

        throw new NoSuchElementException();

    }
}
