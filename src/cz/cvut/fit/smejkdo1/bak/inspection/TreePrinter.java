package cz.cvut.fit.smejkdo1.bak.inspection;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.DecisionTrees;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.common.TreeNode;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram
 */
class TreePrinter {

    public static <T extends Comparable<?>> void printNode(TreeNode<T> root) {
        int maxLevel = TreePrinter.maxLevel(root);

        printNodeInternal(Collections.singletonList(root), 1, maxLevel);
    }

    private static <T extends Comparable<?>> void printNodeInternal(List<TreeNode<T>> nodes, int level, int maxLevel) {
        if (nodes.isEmpty() || TreePrinter.isAllElementsNull(nodes))
            return;

        int floor = maxLevel - level;
        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        TreePrinter.printWhitespaces(firstSpaces);

        List<TreeNode<T>> newNodes = new ArrayList<TreeNode<T>>();
        for (TreeNode<T> node : nodes) {
            if (node != null) {
                System.out.print(node.toString());
                newNodes.add(node.getLeft());
                newNodes.add(node.getRight());
            } else {
                newNodes.add(null);
                newNodes.add(null);
                System.out.print(" ");
            }

            TreePrinter.printWhitespaces(betweenSpaces);
        }
        System.out.println("");

        for (int i = 1; i <= endgeLines; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                TreePrinter.printWhitespaces(firstSpaces - i);
                if (nodes.get(j) == null) {
                    TreePrinter.printWhitespaces(endgeLines + endgeLines + i + 1);
                    continue;
                }

                if (nodes.get(j).hasLeft())
                    System.out.print("/");
                else
                    TreePrinter.printWhitespaces(1);

                TreePrinter.printWhitespaces(i + i - 1);

                if (nodes.get(j).hasRight())
                    System.out.print("\\");
                else
                    TreePrinter.printWhitespaces(1);

                TreePrinter.printWhitespaces(endgeLines + endgeLines - i);
            }

            System.out.println("");
        }

        printNodeInternal(newNodes, level + 1, maxLevel);
    }

    private static void printWhitespaces(int count) {
        for (int i = 0; i < count; i++)
            System.out.print(" ");
    }

    private static <T extends Comparable<?>> int maxLevel(TreeNode<T> node) {
        if (node == null)
            return 0;
        return node.getSubTreeDepth();
    }

    private static <T> boolean isAllElementsNull(List<T> list) {
        for (Object object : list) {
            if (object != null)
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        TreePrinter.printNode(
                ((DecisionTrees) (TransitionBuilder.build(
                        FetchFile.lines(
                                "resources/FiniteAutomatons/Evolved/COMPLEX_EIGHT_VICINITY/INT_TREE/36maps2opponentsNoIADPP300pop60agentsNewRepair/Best/00609.wad"))
                )).getTrees().get(0).getTree().getRoot());
    }
}
