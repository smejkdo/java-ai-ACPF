package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.FMInput;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.BoolTrees;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.DecisionTrees;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.booltree.BoolTreeNodeData;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.inttree.IntTreeNodeData;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;

public class NodeUtil {

    /**
     * Returns deepCopy of generic type A.
     * Needs to have implemented version for used class.
     *
     * @param a   object to copy
     * @param <A> implemented IntTreeNodeData, BoolTreeNodeData, Integer
     * @return if type <A> is implemented returns deepCopy of a, else throws UnsupportedOperationException
     */
    public static <A> A deepCopy(A a) {
        if (a instanceof IntTreeNodeData)
            return (A) ((IntTreeNodeData) a).deepCopy();
        else if (a instanceof BoolTreeNodeData) {
            return (A) ((BoolTreeNodeData) a).deepCopy();
        } else if (a instanceof Integer) {
            return (A) a;
        } else
            throw new UnsupportedOperationException();
    }

    /**
     * Finds out if a is leaf.
     * Needs to have implemented version for used class.
     *
     * @param a   object to check
     * @param <A> implemented IntTreeNodeData, BoolTreeNodeData, Integer
     * @return true if is leaf, else false.
     */
    public static <A> boolean isLeaf(A a) {
        if (a instanceof IntTreeNodeData)
            return (((IntTreeNodeData) a).isLeaf());
        else if (a instanceof BoolTreeNodeData) {
            return ((BoolTreeNodeData) a).isLeaf();
        } else if (a instanceof Integer) {
            return (Integer) a >= 16;
        } else
            throw new UnsupportedOperationException();
    }

    /**
     * Mutates bit of object a.
     * Needs to have implemented version for used class.
     *
     * @param <A>          implemented IntTreeNodeData, BoolTreeNodeData, Integer
     * @param a            object to mutate
     * @param mutationRate rate of mutation
     * @param info         InputStyleUtils for bounds of mutations.
     */
    public static <A> void mutate(A a, int mutationRate, InputStyleUtils info) {
        if (a instanceof IntTreeNodeData)
            ((IntTreeNodeData) a).mutate(mutationRate, info);
        else if (a instanceof BoolTreeNodeData) {
            ((BoolTreeNodeData) a).mutate(mutationRate, info);
        } else
            throw new UnsupportedOperationException();
    }

    public static <A> int difference(A a, A b) {
        int result = 0;
        if (a instanceof IntTreeNodeData)
            result += ((IntTreeNodeData) a).difference(b);
        else if (a instanceof BoolTreeNodeData) {
            result += ((BoolTreeNodeData) a).difference(b);
        } else if (a instanceof Integer) {
            if (b instanceof Integer)
                result += Math.abs((int) a - (int) b);
            else
                result += (int) a * 2;
        } else
            throw new UnsupportedOperationException();
        return result;
    }

    public static <A> int getNewState(A a) {
        if (a instanceof IntTreeNodeData)
            return ((IntTreeNodeData) a).getNewState();
        else if (a instanceof BoolTreeNodeData) {
            return ((BoolTreeNodeData) a).getNewState();
        } else if (a instanceof Integer) {
            return (int) a - 16;
        } else
            throw new UnsupportedOperationException();
    }

    public static <A> boolean transition(A a, FMInput arg) {
        if (a instanceof IntTreeNodeData)
            return ((IntTreeNodeData) a).transition(arg);
        else if (a instanceof BoolTreeNodeData) {
            return ((BoolTreeNodeData) a).transition(arg);
        } else if (a instanceof Integer) {
            return arg.getBit((int) a);
        } else
            throw new UnsupportedOperationException();
    }

    /**
     * Generates new subtree in place of subtree of node. Calls respective methods of classes for doing so.
     *
     * @param node         root of subtree to replace
     * @param mutationRate rate of mutation
     * @param info         info about genotype.
     * @param <A>          implemented IntTreeNodeData, BoolTreeNodeData
     * @return root of new subtree
     */
    public static <A> void generateSubTree(TreeNode<A> node, int mutationRate, InputStyleUtils info) {
        if (node.getA() instanceof IntTreeNodeData)
            DecisionTrees.buildSubTree(node, mutationRate, info);
        else if (node.getA() instanceof BoolTreeNodeData) {
            BoolTrees.buildSubTree(node, mutationRate, info);
        } else
            throw new UnsupportedOperationException();
    }
}
