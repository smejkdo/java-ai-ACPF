package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Tree<A> implements Iterable<A> {
    private TreeNode<A> root;
    public int size;
    public int depth;

    public Tree() {
        size = 0;
        depth = 0;
    }

    public Tree(TreeNode<A> root) {
        this.root = root;
        size = 1;
        depth = 1;

    }

    public TreeNode<A> getRoot() {
        return root;
    }

    public void setRoot(TreeNode<A> root) {
        this.root = root;
    }


    @Override
    public Iterator<A> iterator() {
        return new TreeIterator<>(root);
    }

    public TreeDepthIterator<A> iterator(int depth) {
        return new TreeDepthIterator<>(root, depth);
    }

    public TreeBFSIterator<A> iteratorBFS() {
        return new TreeBFSIterator<>(root);
    }

    @Override
    public void forEach(Consumer<? super A> action) {

    }

    @Override
    public Spliterator<A> spliterator() {
        return null;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        TreeNode<A> node;


        for (TreeBFSIterator<A> it = iteratorBFS(); it.hasNext(); ) {
            node = it.peekNextNode();
            it.next();
            sb.append(node.toString()).append("\n");
        }
        return sb.toString();
    }

    public Tree<A> deepCopy() {
        Tree<A> tree = new Tree<>(root.deepCopy(null));
        tree.size = tree.root.getSubTreeSize();
        tree.depth = tree.root.getSubTreeDepth();
        return tree;
    }

    public void refreshSize() {
        root.refreshSizeDown();
        size = root.getSubTreeSize();
        depth = root.getSubTreeDepth();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tree)) return false;
        Tree<?> tree = (Tree<?>) o;
        return size == tree.size &&
                depth == tree.depth &&
                root.equals(tree.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, size, depth);
    }
}
