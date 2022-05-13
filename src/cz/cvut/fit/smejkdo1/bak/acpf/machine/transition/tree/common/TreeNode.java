package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.booltree.BoolTreeNodeData;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.inttree.IntTreeNodeData;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.inttree.TreeRepairList;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TreeNode<A> {
    private A a;
    private int subTreeDepth;
    private int subTreeSize;
    private TreeNode<A> right;
    private TreeNode<A> left;
    private TreeNode<A> parent;

    public TreeNode(TreeNode<A> parent, A a) {
        this.parent = parent;
        this.a = a;
        this.subTreeDepth = 1;
        this.subTreeSize = 1;
    }


    public int[] refreshSizeDown() {
        int[] list;
        subTreeSize = 1;
        subTreeDepth = 1;

        if (hasLeft()) {
            list = getLeft().refreshSizeDown();
            subTreeSize += list[0];
            subTreeDepth += list[1];
        }
        if (hasRight()) {
            list = getRight().refreshSizeDown();
            subTreeSize += list[0];
            subTreeDepth = Math.max(list[1] + 1, subTreeDepth);
        }
        return new int[]{subTreeSize, subTreeDepth};
    }


    /**
     * Adds child node from left to right.
     *
     * @param node node to connect
     * @return true if child was added, else false.
     */
    public boolean addChild(TreeNode<A> node) {
        if (NodeUtil.isLeaf(this.getA()))
            return false;
        if (!hasLeft()) {
            this.setLeft(node);
            node.setParent(this);
            return true;
        } else if (!hasRight()) {
            this.setRight(node);
            node.setParent(this);
            return true;
        }
        return false;
    }

    public boolean addChild(A a) {
        if (NodeUtil.isLeaf(this.getA()))
            return false;
        return addChild(new TreeNode<>(this, a));
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public TreeNode<A> getRight() {
        return right;
    }

    public void setRight(TreeNode<A> right) {
        this.right = right;
    }

    public boolean hasRight() {
        return right != null;
    }

    public TreeNode<A> getLeft() {
        return left;
    }

    public void setLeft(TreeNode<A> left) {
        this.left = left;
    }

    public boolean hasLeft() {
        return left != null;
    }

    public TreeNode<A> getParent() {
        return parent;
    }

    public void setParent(TreeNode<A> parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public int getSubTreeDepth() {
        return subTreeDepth;
    }

    public void setSubTreeDepth(int subTreeDepth) {
        this.subTreeDepth = subTreeDepth;
    }

    public int getSubTreeSize() {
        return subTreeSize;
    }

    public void setSubTreeSize(int subTreeSize) {
        this.subTreeSize = subTreeSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreeNode)) return false;
        TreeNode<?> treeNode = (TreeNode<?>) o;
        return subTreeDepth == treeNode.subTreeDepth &&
                subTreeSize == treeNode.subTreeSize &&
                a.equals(treeNode.a) &&
                Objects.equals(right, treeNode.right) &&
                Objects.equals(left, treeNode.left);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, subTreeDepth, subTreeSize);
    }

    @Override
    public String toString() {
        return a.toString();
    }

    public TreeNode<A> deepCopy(TreeNode<A> parent) {
        TreeNode<A> result = new TreeNode<>(parent, NodeUtil.deepCopy(a));
        result.subTreeDepth = this.subTreeDepth;
        result.subTreeSize = this.subTreeSize;

        if (hasLeft())
            result.setLeft(this.left.deepCopy(result));
        if (hasRight())
            result.setRight(this.right.deepCopy(result));
        return result;
    }

    /**
     * Count depth of this node
     *
     * @return depth
     */
    public int getDepth() {
        TreeNode<A> n = this;
        int depth = 0;
        while (n.hasParent()) {
            n = n.getParent();
            depth++;
        }
        return depth;
    }

    public int difference(TreeNode<A> oth) {
        return NodeUtil.difference(a, oth.a)
                + Math.abs(subTreeDepth - oth.subTreeDepth)
                + Math.abs(subTreeSize - oth.subTreeSize)
                + childrenDifference(oth);

    }

    private int childrenDifference(TreeNode<A> oth) {
        int result = 0;
        if (this.hasLeft() && oth.hasLeft())
            result += this.getLeft().difference(oth.getLeft());
        else if (this.hasLeft())
            result += this.getLeft().subTreeSize * 2;
        else if (oth.hasLeft())
            result += oth.getLeft().subTreeSize * 2;

        if (this.hasRight() && oth.hasRight())
            result += this.getRight().difference(oth.getRight());
        else if (this.hasRight())
            result += this.getRight().subTreeSize * 2;
        else if (oth.hasRight())
            result += oth.getRight().subTreeSize * 2;
        return result;
    }

    public void swap(TreeNode<A> oth) {
        TreeNode<A> thisParent = this.parent;
        TreeNode<A> othParent = oth.parent;
        this.setParent(othParent);
        oth.setParent(thisParent);

        if (thisParent != null) {
            if (thisParent.getLeft().equals(this))
                thisParent.setLeft(oth);
            else
                thisParent.setRight(oth);
        }
        if (othParent != null) {
            if (othParent.getLeft().equals(oth))
                othParent.setLeft(this);
            else
                othParent.setRight(this);
        }
    }

    /**
     * Removes unreachable nodes in the tree, as well as nodes not changing outcome.
     * Only used with IntTreeNodeData.
     *
     * @param repairList list mapping what values can reach this node.
     * @return list of results this subtree can yield
     */
    public Set<Integer> repairIntDown(TreeRepairList repairList) {
        if (a instanceof IntTreeNodeData) {
            if (((IntTreeNodeData) a).isLeaf()) {
                Set<Integer> result = new HashSet<>();
                result.add(((IntTreeNodeData) a).getArgThreshold());
                return result;
            } else {
                TreeRepairList leftList = repairList.deepCopy();
                TreeRepairList rightList = repairList.deepCopy();
                int argNum = ((IntTreeNodeData) this.getA()).getArgNumber();
                int argThreshold = ((IntTreeNodeData) this.getA()).getArgThreshold();
                if (repairList.get(argNum).getKey() <= argThreshold) {
                    leftList.set(argNum,
                            new Pair<>(
                                    repairList.get(argNum).getKey(),
                                    argThreshold));
                } else {
                    if (repairList.get(argNum).getKey().equals(repairList.get(argNum).getValue())) {
                        replaceThisNode(getRight());
                        return this.repairIntDown(repairList);
                    } else {
                        ((IntTreeNodeData) this.getA()).setArgThreshold(repairList.get(argNum).getKey());
                        argThreshold = ((IntTreeNodeData) this.getA()).getArgThreshold();
                    }
                }
                if (repairList.get(argNum).getValue() > argThreshold)
                    rightList.set(argNum,
                            new Pair<>(
                                    argThreshold + 1,
                                    repairList.get(argNum).getValue()));
                else {
                    if (repairList.get(argNum).getKey().equals(repairList.get(argNum).getValue())) {
                        replaceThisNode(getLeft());
                        return this.repairIntDown(repairList);
                    } else {
                        ((IntTreeNodeData) this.getA()).setArgThreshold(repairList.get(argNum).getValue() - 1);
                        argThreshold = ((IntTreeNodeData) this.getA()).getArgThreshold();
                    }
                }
                Set<Integer> result = this.getLeft().repairIntDown(leftList);
                result.addAll(this.getRight().repairIntDown(rightList));
                if (result.size() == 1) {
                    replaceThisNode(new TreeNode<>(this.parent, (A) new IntTreeNodeData(
                            (Integer) result.toArray()[0])));
                }
                return result;
            }

        } else
            throw new UnsupportedOperationException();
    }

    private void replaceThisNode(TreeNode<A> replacement) {
        this.setA(replacement.getA());
        this.setLeft(replacement.getLeft());
        this.setRight(replacement.getRight());
        if (hasLeft())
            getLeft().setParent(this);
        if (hasRight())
            getRight().setParent(this);
    }

    private void removeThisNode(TreeNode<A> replacement) {
        replacement.parent = this.parent;
        if (hasParent()) {
            if (this.parent.hasLeft()
                    && this.parent.getLeft().equals(this))
                parent.left = replacement;
            else
                parent.right = replacement;
        }
    }

    public void repairBoolDown() {
        if (!(this.a instanceof BoolTreeNodeData))
            throw new UnsupportedOperationException();

        if (this.hasParent()) {
            int tmpInputLength = ((BoolTreeNodeData) this.getParent().getA()).inputLength - 1;
            if (tmpInputLength != ((BoolTreeNodeData) a).inputLength) {
                if (tmpInputLength == 0 && hasRight()) { //expected to be more likely false
                    replaceThisNode(getRight());
                    this.repairBoolDown();
                    return;
                }
                ((BoolTreeNodeData) a).changeInputLength(tmpInputLength);
            }
        }
        if (((BoolTreeNodeData) a).isLeaf()) {
            if (hasRight() || hasLeft()) {
                right = null;
                left = null;
            }
        } else {
            if (hasRight() && hasLeft()) {
                right.repairBoolDown();
                left.repairBoolDown();
            } else {
                ((BoolTreeNodeData) a).generateLeaf();
                right = null;
                left = null;
            }
        }
    }

    /**
     * from https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram
     * creates horizontal visualisation of tree
     *
     * @return visualisation of tree
     */
    public String visualisation() {
        StringBuilder sb = new StringBuilder();
        print(sb, "", "");
        return sb.toString();
    }

    private void print(StringBuilder sb, String prefix, String childrenPrefix) {
        sb.append(prefix);
        sb.append(toString());
        sb.append('\n');
        if (hasRight()) {
            if (hasLeft()) {
                getRight().print(sb, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                getRight().print(sb, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
            if (hasLeft())
                getLeft().print(sb, childrenPrefix + "└── ", childrenPrefix + "    ");
        }

    }
}
