package cz.cvut.fit.smejkdo1.bak.inspection;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.*;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SolutionInspector {
    private final int states;
    private TransitionInterface solution;
    private List<List<Integer>> deconstructed;
    private String saveLocation;
    private String name;
    private InputStyleUtils isu;

    public SolutionInspector(TransitionInterface solution,
                             String saveLocation, String name, int states) {
        this.solution = solution;
        this.saveLocation = saveLocation;
        this.name = name;
        this.states = states;
        this.isu = InputStyleUtils.getInstance(solution.getInputStyle());
    }

    public static void main(String[] args) {
        SolutionInspector inspector = new SolutionInspector(
                TransitionBuilder.build(
                        FetchFile.lines(
                                "resources/FiniteAutomatons/Evolved/EIGHT_VICINITY/TRANSITION_LIST/map1/Best/00022.wad")),
                "resources/SolutionsToAnalyze/", "tlonmap1", 4);
        inspector.deconstruct();
    }

    public void deconstruct() {
        switch (solution.getOutputStyle()) {
            case TRANSITION_LIST:
                deconstructTL();
                break;
            case BOOL_TREE:
                deconstructBT();
                break;
            case INT_TREE:
                deconstructIT();
                break;
            default:
                throw new IllegalArgumentException(solution.getInputStyle().toString());
        }
    }

    private void deconstructIT() {
        DecisionTrees bt = (DecisionTrees) solution;
        FetchFile.save(bt.visualisation(), saveLocation, name + ".csv");
    }

    private void deconstructBT() {
        BoolTrees bt = (BoolTrees) solution;
        FetchFile.save(bt.visualisation(), saveLocation, name + ".csv");
    }

    private void deconstructTL() {
        List<Integer> prefixes = preparePrefixes();
        List<Integer> masks = prepareMasks(prefixes);
        Collections.reverse(prefixes);
        List<List<Integer>> list = new ArrayList<>();
        int bound = 1 << isu.getNumberOfBits();
        for (int i = 0; i < bound; i++) {
            list.add(new ArrayList<>());
            for (int j = 0; j < states; j++) {
                list.get(i).add(((TransitionList) solution).getTransitions().get(j).get(i));
            }
            for (int j = 0; j < masks.size(); j++) {
                int arg = (i & masks.get(j));
                arg = arg >> prefixes.get(j);
                list.get(i).add(arg);
            }
        }

        deconstructed = list;
        generateByArgumentsCSV();
        generateByBitsCSV();
    }

    private void generateByBitsCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstLineBits());
        int bound = 1 << isu.getNumberOfBits();

        for (int i = 0; i < bound; i++) {
            for (int j = 0; j < states; j++) {
                sb.append(((TransitionList) solution)
                        .getTransitions().get(j).get(i))
                        .append(";");
            }
            String bits = Integer.toBinaryString(i);
            for (int j = 0; j < isu.getNumberOfBits(); j++) {
                char arg = j < bits.length() ? (bits.charAt(j)) : '0';
                sb.append(arg);
                if (j == isu.getNumberOfBits() - 1)
                    sb.append("\n");
                else
                    sb.append(";");
            }
        }

        FetchFile.save(sb.toString(), saveLocation, name + "_bits.csv");
    }

    private String firstLineBits() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < states; i++) {
            sb.append(i).append(";");
        }
        int bound = isu.getNumberOfBits();
        for (int i = 0; i < bound; i++) {
            sb.append("BIT_").append(i);
            if (i == bound - 1)
                sb.append("\n");
            else
                sb.append(";");
        }
        return sb.toString();
    }

    private void generateByArgumentsCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstLine());
        for (List<Integer> list :
                deconstructed) {
            for (int j = 0; j < list.size(); j++) {
                sb.append(list.get(j));
                if (j == list.size() - 1)
                    sb.append("\n");
                else
                    sb.append(";");

            }
        }
        FetchFile.save(sb.toString(), saveLocation, name + ".csv");
    }

    private String firstLine() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < states; i++) {
            sb.append(i).append(";");
        }
        sb.append(InputStyleUtils.csvTags(solution.getInputStyle()))
                .append("\n");
        return sb.toString();
    }

    private List<Integer> prepareMasks(List<Integer> prefixes) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < prefixes.size(); i++) {
            int mask = ((1 << isu.getMaxLengthOfArgumentInBits(i)) - 1)
                    << prefixes.get(prefixes.size() - 1 - i);
            result.add(mask);
        }
        return result;
    }

    private List<Integer> preparePrefixes() {
        List<Integer> result = new ArrayList<>();
        int prefix = 0;
        for (int i = 0; i < isu.getNumberOfArguments(); i++) {
            result.add(prefix);
            prefix += isu.getMaxLengthOfArgumentInBits(isu.getNumberOfArguments() - 1 - i);
        }
        return result;
    }
}
