package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.FMInput;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common.Tree;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common.TreeManager;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common.TreeNode;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.inttree.IntTreeNodeData;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.inttree.TreeRepairList;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//Works with up to 16 arguments in input.
public class DecisionTrees implements Serializable, TransitionInterface {
    private List<TreeManager<IntTreeNodeData>> trees = new ArrayList<>();
    private InputStyleUtils inputStyleUtils;

    public DecisionTrees() {
    }

    public DecisionTrees(int states) {
        for (int i = 0; i < states; i++) {
            trees.add(new TreeManager<>());
        }
    }

    public static DecisionTrees build(List<String> strList) {
        DecisionTrees tree = new DecisionTrees();
        tree.inputStyleUtils = InputStyleUtils.getInstance(InputStyle.valueOf(strList.get(1)));
        int j = 2;
        for (int i = 2; i < strList.size(); i++) {
            if (strList.get(i).equals("")) {
                tree.trees.add(
                        TreeManager.build(
                                IntTreeNodeData.buildList(
                                        strList.subList(j, i)
                                )));
                j = i + 1;
            }
        }
        tree.trees.add(
                TreeManager.build(
                        IntTreeNodeData.buildList(
                                strList.subList(j, strList.size())
                        )));
        tree.trees.forEach(TreeManager::refreshSize);
        return tree;
    }

    public static <A> void buildSubTree(TreeNode<A> root, int mutationProbability, InputStyleUtils info) {
        if (!(root.getA() instanceof IntTreeNodeData))
            throw new UnsupportedOperationException();
        IntTreeNodeData node;
        TreeManager<IntTreeNodeData> tree = new TreeManager<>();
        int mutationRate = Math.max(0, 60 - root.getSubTreeSize() * (Rand.nextInt(6) + 1));
        //System.out.print("|" + mutationRate + ": " + root.getSubTreeSize() + "|");
        int states = 4; //number of states
        do {
            if (Rand.nextInt(100) < mutationRate) {
                node = new IntTreeNodeData(Rand.nextInt(states));
            } else {
                int argNum = Rand.nextInt(info.getNumberOfArguments());
                int maxArg = 1 << info.getMaxLengthOfArgumentInBits(argNum);
                node = new IntTreeNodeData(argNum, Rand.nextInt(maxArg));
            }
            mutationRate += mutationProbability;
            mutationRate = Math.min(mutationRate, 60 + root.getDepth() * 2);
        } while (tree.addNode(node));
        TreeNode<A> newRoot = (TreeNode<A>) tree.getTree().getRoot(); //is checked
        root.setA(newRoot.getA());
        root.setLeft(newRoot.getLeft());
        root.setRight(newRoot.getRight());
        root.refreshSizeDown();
        //System.out.println(root.getSubTreeSize());
    }

    public List<TransitionInterface> cross(DecisionTrees oth) {
        List<TransitionInterface> children = new ArrayList<>();
        children.add(this.deepCopy());
        children.add(oth.deepCopy());

        int split = Rand.nextInt(trees.size());
        ((DecisionTrees) (children.get(0))).trees.get(split).cross(((DecisionTrees) (children.get(1))).trees.get(split));

        for (int i = split + 1; i < trees.size(); i++) {
            TreeManager<IntTreeNodeData> tmp = ((DecisionTrees) (children.get(0))).trees.get(i);
            ((DecisionTrees) (children.get(0))).trees.set(i, ((DecisionTrees) (children.get(1))).trees.get(i));
            ((DecisionTrees) (children.get(1))).trees.set(i, tmp);
        }
        children.forEach(child -> ((DecisionTrees) child).trees.forEach(TreeManager::refreshSize));
        return children;
    }


    @Override
    public void fillRand(int mutationProbability, InputStyleUtils inputStyleUtils) {
        mutationProbability = Math.min(mutationProbability, 60);
        int mutationRate = 0;
        IntTreeNodeData node;
        this.inputStyleUtils = inputStyleUtils;
        for (TreeManager<IntTreeNodeData> tree : trees) {
            mutationRate = 0;
            do {
                if (Rand.nextInt(100) < mutationRate) {
                    node = new IntTreeNodeData(Rand.nextInt(trees.size()));
                } else {
                    int argNum = Rand.nextInt(inputStyleUtils.getNumberOfArguments());
                    int maxArg = 1 << inputStyleUtils.getMaxLengthOfArgumentInBits(argNum);
                    node = new IntTreeNodeData(argNum, Rand.nextInt(maxArg));
                }
                mutationRate += mutationProbability;
                mutationRate = Math.min(mutationRate, 60);
            } while (tree.addNode(node));
            tree.refreshSize();
        }
    }

    @Override
    public void mutateBit(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void mutate(int mutationRate) {
        for (TreeManager<IntTreeNodeData> tree : trees) {
            tree.mutate(mutationRate, inputStyleUtils);
            tree.refreshSize();
        }
    }

    @Override
    public int transition(FMInput arg, int state) {
        return trees.get(state).transition(arg);
    }

    @Override
    public OutputStyle getOutputStyle() {
        return OutputStyle.INT_TREE;
    }

    @Override
    public InputStyle getInputStyle() {
        return inputStyleUtils.getInputStyle();

    }

    public List<TreeManager<IntTreeNodeData>> getTrees() {
        return trees;
    }

    public void setTrees(List<TreeManager<IntTreeNodeData>> trees) {
        this.trees = trees;
    }

    public int getStates() {
        return trees.size();
    }

    @Override
    public void save(String fileName, String filePath) {
        FetchFile.save(transitionRepresentation(), filePath, fileName);
    }

    private String transitionRepresentation() {
        return toString();
    }

    public String visualisation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < trees.size(); i++) {
            sb.append("STATE ")
                    .append(i).append(":\n")
                    .append(trees.get(i).visualisation())
                    .append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public int getNumberOfBits() {
        int result = 0;
        for (TreeManager<IntTreeNodeData> tree : trees) {
            result += tree.getSize()
                    * (IntTreeNodeData.ARG_NUMBER_MAX_BITS
                    + IntTreeNodeData.ARG_THRESHOLD_MAX_BITS);
        }
        return result;
    }

    @Override
    public TransitionInterface deepCopy() {
        DecisionTrees result = new DecisionTrees();
        result.trees = trees.stream().map(TreeManager::deepCopy).collect(Collectors.toList());
        result.inputStyleUtils = this.inputStyleUtils;
        return result;
    }

    @Override
    public List<TransitionInterface> crossover(TransitionInterface genotype) {
        if (!(genotype instanceof DecisionTrees)) {
            throw new UnsupportedOperationException();
        }
        return cross((DecisionTrees) genotype);
    }

    @Override
    public void repairSelf() {
        for (Tree<IntTreeNodeData> tree :
                trees.stream()
                        .map(TreeManager::getTree)
                        .collect(Collectors.toList())) {
            repair(tree.getRoot());
        }
        trees.forEach(TreeManager::refreshSize);
    }

    private void repair(TreeNode<IntTreeNodeData> root) {
        TreeRepairList repairList = new TreeRepairList(inputStyleUtils);
        root.repairIntDown(repairList);
    }

    @Override
    public int difference(TransitionInterface other) {
        int result = 0;
        if (!other.getClass().equals(this.getClass()))
            return this.getNumberOfBits();
        for (int i = 0; i < trees.size(); i++) {
            result += trees.get(i).difference(((DecisionTrees) other).trees.get(i));
        }
        return result;
    }

    @Override
    public void symmetry(int[] bits1, int[] bits2, int[] args1, int[] args2) {

    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OutputStyle.INT_TREE).append("\n");
        sb.append(inputStyleUtils.toString()).append("\n");
        for (TreeManager<IntTreeNodeData> tree :
                trees) {
            sb.append(tree.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecisionTrees)) return false;
        DecisionTrees that = (DecisionTrees) o;
        return Objects.equals(trees, that.trees) &&
                Objects.equals(inputStyleUtils, that.inputStyleUtils);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trees, inputStyleUtils);
    }
}
