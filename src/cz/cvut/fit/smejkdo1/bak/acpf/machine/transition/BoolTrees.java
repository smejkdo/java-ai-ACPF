package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.FMInput;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.booltree.BoolTreeNodeData;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common.TreeManager;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common.TreeNode;
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
import java.util.stream.IntStream;

public class BoolTrees implements TransitionInterface, Serializable {
    private List<TreeManager<BoolTreeNodeData>> trees = new ArrayList<>();
    private InputStyleUtils inputStyleUtils;

    public BoolTrees() {
    }

    public BoolTrees(int states) {
        for (int j = 0; j < states; j++) {
            trees.add(new TreeManager<>());
        }
    }

    public static BoolTrees build(List<String> strList) {
        BoolTrees tree = new BoolTrees();
        tree.inputStyleUtils = InputStyleUtils.getInstance(InputStyle.valueOf(strList.get(1)));
        int j = 2;
        for (int i = 2; i < strList.size(); i++) {
            if (strList.get(i).equals("")) {
                List<BoolTreeNodeData> list = BoolTreeNodeData
                        .buildList(strList.subList(j, i), tree.inputStyleUtils);
                tree.trees.add(
                        TreeManager.build(list));
                j = i + 1;
            }
        }
        tree.trees.add(
                TreeManager.build(
                        BoolTreeNodeData.buildList(
                                strList.subList(j, strList.size()),
                                tree.inputStyleUtils
                        )));
        tree.trees.forEach(TreeManager::refreshSize);
        tree.repairSelf();
        return tree;
    }

    public static <A> void buildSubTree(TreeNode<A> root, int mutationProbability, InputStyleUtils info) {

        if (!(root.getA() instanceof BoolTreeNodeData))
            throw new UnsupportedOperationException();
        BoolTreeNodeData node;
        TreeManager<BoolTreeNodeData> tree = new TreeManager<>();
        int mutationRate = Math.max(0, 60 - root.getSubTreeSize() * (Rand.nextInt(6) + 1));
        //System.out.print("|" + mutationRate + ": " + root.getSubTreeSize() + "|");
        int depth;
        int states = 4; //number of states
        do {
            depth = tree.getDepth() + root.getDepth();
            if (Rand.nextInt(100) < mutationRate
                    || depth >= info.getNumberOfBits()) {
                node = new BoolTreeNodeData(
                        (info.getNumberOfBits() - depth + Rand.nextInt(states)),
                        info.getNumberOfBits() - depth, states);
            } else {
                node = new BoolTreeNodeData(
                        Rand.nextInt(info.getNumberOfBits() - depth),
                        info.getNumberOfBits() - depth, states);
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

    public void buildRandTree(int mutationProbability, InputStyleUtils inputStyleUtils) {
        mutationProbability = Math.min(mutationProbability, 60);
        int mutationRate = 0;
        BoolTreeNodeData node;
        this.inputStyleUtils = inputStyleUtils;
        for (TreeManager<BoolTreeNodeData> tree : trees) {
            mutationRate = 0;
            int depth;
            do {
                depth = tree.getDepth();
                if (Rand.nextInt(100) < mutationRate
                        || depth >= inputStyleUtils.getNumberOfBits()) {
                    node = new BoolTreeNodeData(
                            (inputStyleUtils.getNumberOfBits() - depth + Rand.nextInt(trees.size())),
                            inputStyleUtils.getNumberOfBits() - depth, trees.size());
                } else {
                    node = new BoolTreeNodeData(
                            Rand.nextInt(inputStyleUtils.getNumberOfBits() - depth),
                            inputStyleUtils.getNumberOfBits() - depth, trees.size());
                }
                mutationRate += mutationProbability;
                mutationRate = Math.min(mutationRate, 80);
            } while (tree.addNode(node));
            tree.refreshSize();
        }
        repairSelf();
    }


    @Override
    public int transition(FMInput arg, int state) {
        List<Integer> availableBits = IntStream.range(0, inputStyleUtils.getNumberOfBits())
                .boxed().collect(Collectors.toList());

        TreeNode<BoolTreeNodeData> node = trees
                .get(state).getTree().getRoot();

        while (!node.getA().isLeaf()) {
            int bitNum = node.getA().getArgNumber();
            if (bitNum < 0 || bitNum >= availableBits.size()) {
                System.err.println(node.toString() + " " + node.getDepth() + " " + node.getA().inputLength);
            }
            if (arg.getBit(
                    availableBits.get(bitNum)))
                node = node.getLeft();
            else
                node = node.getRight();
            availableBits.remove(bitNum);
        }
        return node.getA().getNewState();
    }

    @Override
    public OutputStyle getOutputStyle() {
        return OutputStyle.BOOL_TREE;
    }

    @Override
    public InputStyle getInputStyle() {
        return inputStyleUtils.getInputStyle();
    }

    @Override
    public void mutateBit(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void mutate(int mutationRate) {
        for (TreeManager<BoolTreeNodeData> tree : trees) {
            tree.mutate(mutationRate, inputStyleUtils); // hardcoded number of bits in input
            tree.refreshSize();
        }
    }

    @Override
    public void fillRand(int mutationProbability, InputStyleUtils inputStyleUtils) {
        buildRandTree(mutationProbability, inputStyleUtils);
    }

    @Override
    public void save(String fileName, String filePath) {
        FetchFile.save(transitionRepresentation(), filePath, fileName);
    }

    private String transitionRepresentation() {
        return toString();
    }

    @Override
    public int getNumberOfBits() {
        return trees.stream().mapToInt(tree -> tree.getSize() * 6).sum();
    }

    @Override
    public TransitionInterface deepCopy() {
        BoolTrees result = new BoolTrees();
        result.trees = trees.stream().map(TreeManager::deepCopy).collect(Collectors.toList());
        result.inputStyleUtils = this.inputStyleUtils;
        return result;
    }

    @Override
    public List<TransitionInterface> crossover(TransitionInterface genotype) {
        if (!(genotype instanceof BoolTrees)) {
            throw new UnsupportedOperationException();
        }
        return cross((BoolTrees) genotype);
    }

    private List<TransitionInterface> cross(BoolTrees oth) {
        List<TransitionInterface> children = new ArrayList<>();
        children.add(this.deepCopy());
        children.add(oth.deepCopy());

        int split = Rand.nextInt(trees.size());
        ((BoolTrees) (children.get(0))).trees.get(split).cross(oth.trees.get(split));

        for (int i = split + 1; i < trees.size(); i++) {
            TreeManager<BoolTreeNodeData> tmp = ((BoolTrees) (children.get(0))).trees.get(i);
            ((BoolTrees) (children.get(0))).trees.set(i, ((BoolTrees) (children.get(1))).trees.get(i));
            ((BoolTrees) (children.get(1))).trees.set(i, tmp);
        }
        children.forEach(child -> ((BoolTrees) child).trees.forEach(TreeManager::refreshSize));
        return children;
    }

    public String visualisation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < trees.size(); i++) {
            sb.append("STATE ")
                    .append(i).append(":\n");
            for (int j = 0; j < trees.get(i).getDepth(); j++) {
                sb.append(j + 1)
                        .append(" "
                                .repeat(4 - Integer
                                        .toString(j + 1)
                                        .length()));
            }
            sb.append(":depths\n");

            sb.append(trees.get(i).visualisation())
                    .append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public void repairSelf() {
        this.trees.forEach(tree -> {
            ((BoolTreeNodeData) tree.getTree().getRoot().getA()).changeInputLength(inputStyleUtils.getNumberOfBits());
            tree.getTree().getRoot().repairBoolDown();
            tree.refreshSize();
        });
    }

    @Override
    public int difference(TransitionInterface other) {
        int result = 0;
        if (!other.getClass().equals(this.getClass()))
            return this.getNumberOfBits();
        for (int i = 0; i < trees.size(); i++) {
            result += trees.get(i).difference(((BoolTrees) other).trees.get(i));
        }
        return result;
    }

    @Override
    public void symmetry(int[] bits1, int[] bits2, int[] args1, int[] args2) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoolTrees boolTrees = (BoolTrees) o;
        return Objects.equals(trees, boolTrees.trees) &&
                Objects.equals(inputStyleUtils, boolTrees.inputStyleUtils);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trees, inputStyleUtils);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OutputStyle.BOOL_TREE).append("\n");
        sb.append(inputStyleUtils.toString()).append("\n");
        for (TreeManager<BoolTreeNodeData> tree :
                trees) {
            sb.append(tree.toString()).append("\n");
        }
        return sb.toString();
    }

    public List<TreeManager<BoolTreeNodeData>> getTrees() {
        return trees;
    }
}
