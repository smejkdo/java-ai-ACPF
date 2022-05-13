package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.FMInput;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;

import java.util.List;
import java.util.Objects;

public class TreeManager<A> {
    private Tree<A> tree;
    private TreeBFSIterator<A> iterator;

    public TreeManager() {
    }

    public static <A> TreeManager<A> build(List<A> AList) {

        TreeManager<A> treeManager = new TreeManager<>();
        TreeNode<A> node = new TreeNode<>(null, AList.get(0));
        treeManager.tree = new Tree<>(node);
        int i = 1;

        for (TreeBFSIterator<A> it = treeManager.tree.iteratorBFS(); it.hasNext() && i < AList.size(); ) {
            node = it.peekNextNode();
            for (int j = 0; j < 2; j++) {
                if (node.addChild(AList.get(i))) {
                    i++;
                }
            }
            it.next();
        }
        return treeManager;
    }


    /**
     * adds new node into upcoming position in tree.
     *
     * @param a value to put in treeNode
     * @return returns true if value was added successfully, else false
     */
    public boolean addNode(A a) {
        if (tree == null) {
            tree = new Tree<>(new TreeNode<>(null, a));
            tree.depth = 1;
            return true;
        }

        if (iterator == null) {
            iterator = tree.iteratorBFS();
        }

        if (!iterator.hasNext())
            return false;
        TreeNode<A> node = iterator.peekNextNode();

        while (!node.addChild(new TreeNode<>(node, a))) {
            iterator.next();
            if (!iterator.hasNext())
                return false;
            node = iterator.peekNextNode();
        }
        tree.size++;
        tree.depth = node.getDepth() + 2;
        return true;
    }

    public int transition(FMInput arg) {
        TreeNode<A> node = tree.getRoot();
        while (!NodeUtil.isLeaf(node.getA())) {
            if (NodeUtil.transition(node.getA(), arg))
                node = node.getLeft();
            else
                node = node.getRight();
        }
        return NodeUtil.getNewState(node.getA());
    }

    public void refreshSize() {
        tree.refreshSize();
    }

    public TreeManager<A> deepCopy() {
        TreeManager<A> result = new TreeManager<>();
        result.tree = this.tree.deepCopy();
        //result.iterator iterator should not be needed
        return result;
    }

    public void mutate(int mutationRate, InputStyleUtils info) {
        for (TreeBFSIterator<A> it = tree.iteratorBFS(); it.hasNext(); ) {
            TreeNode<A> node = it.peekNextNode();
            if (node.hasParent()
                    && Rand.nextInt(100) < mutationRate
                    && Rand.nextInt(100) < mutationRate) {
                NodeUtil.generateSubTree(node, 2 + Rand.nextInt(60), info);
                A a = it.next();
            } else {
                A a = it.next();
                NodeUtil.mutate(a, mutationRate, info);
            }
        }
    }


    @Override
    public String toString() {
        return tree.toString();
    }

    public int getDepth() {
        if (tree == null)
            return 0;
        else
            return tree.depth;
    }

    public int getSize() {
        if (tree == null)
            return 0;
        else
            return tree.size;
    }

    public Tree<A> getTree() {
        return tree;
    }

    public int difference(TreeManager<A> oth) {
        int result = 0;

        result += tree.getRoot().difference(oth.getTree().getRoot());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreeManager)) return false;
        TreeManager<?> that = (TreeManager<?>) o;
        return Objects.equals(tree, that.tree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tree);
    }

    public void cross(TreeManager<A> oth) {
        int split;
        if (this.tree.size < oth.tree.size)
            split = Rand.nextInt(this.tree.size);
        else
            split = Rand.nextInt(oth.tree.size);

        TreeBFSIterator<A> itOth = oth.tree.iteratorBFS();
        TreeBFSIterator<A> itThis = this.tree.iteratorBFS();
        for (int i = 0; itThis.hasNext() && itOth.hasNext(); i++) {
            if (i < split) {
                itThis.next();
                itOth.next();
            } else {
                break;
            }
        }

        TreeNode<A> thisNode = itThis.peekNextNode();
        TreeNode<A> othNode = itOth.peekNextNode();
        int thisDepth = thisNode.getDepth();
        int othDepth = othNode.getDepth();
        while (itThis.hasNext() && itOth.hasNext()
                && itThis.peekNextNode().getDepth() == thisDepth
                && itOth.peekNextNode().getDepth() == othDepth) {
            thisNode = itThis.peekNextNode();
            othNode = itOth.peekNextNode();
            thisNode.swap(othNode);


            itThis.next();
            itOth.next();
        }
    }

    public String visualisation() {
        return tree.getRoot().visualisation();
    }

}
