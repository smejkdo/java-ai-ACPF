package cz.cvut.fit.smejkdo1.bak.inspection;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProgressEvaluator {
    public static void main(String[] args) {
        //singleRun(InputStyle.EIGHT_VICINITY, OutputStyle.TRANSITION_LIST, "_test");
        for (int i = 2; i < 7; i++) {
            singleRun(InputStyle.COMPLEX_EIGHT_VICINITY, OutputStyle.INT_TREE, "_run" + i);
        }

    }

    public static void allRun() {
        for (InputStyle inputStyle : InputStyle.values()) {
            if (inputStyle.equals(InputStyle.INFO_EXCHANGE))
                continue;
            for (OutputStyle outputStyle : OutputStyle.values()) {
                if (inputStyle.equals(InputStyle.COMPLEX_EIGHT_VICINITY) && outputStyle.equals(OutputStyle.TRANSITION_LIST))
                    continue;
                System.out.println(inputStyle + " : " + outputStyle);
                singleRun(inputStyle, outputStyle, "_runN");
            }
        }
    }

    public static void singleRun(InputStyle inputStyle, OutputStyle outputStyle, String suffix) {
        List<Pair<String, List<TransitionInterface>>> solutions = fetchSolutions(inputStyle, outputStyle);
        double start = System.currentTimeMillis();
        if (solutions != null)
            for (Pair<String, List<TransitionInterface>> stringListPair : solutions) {
                List<TransitionInterface> solution = stringListPair.getValue();
                String name = stringListPair.getKey();
                EvaluationData ed = EvaluationData.evaluate(solution);
                FetchFile.save(ed.toString(), "resources/EvolutionProgress/" + "ADPPOpponent/"
                        + inputStyle + "/" + outputStyle + "/", name + suffix + ".csv");
                System.out.println(name);
            }
        double end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
    }

    private static List<Pair<String, List<TransitionInterface>>> fetchSolutions(InputStyle inputStyle, OutputStyle outputStyle) {
        String evolvedFolder = "resources/FiniteAutomatons/Evolved"; //hardcoded path to evolved solutions
        File evolvedFile = new File(evolvedFolder);
        if (!evolvedFile.isDirectory())
            throw new UnsupportedOperationException("File is not a directory.");

        for (File f : Objects.requireNonNull(evolvedFile.listFiles())) {
            if (f.getName().equals(inputStyle.name())) {
                return searchInInputStyleFolder(f, inputStyle, outputStyle);
            }
        }
        return null;
    }

    private static List<Pair<String, List<TransitionInterface>>> searchInInputStyleFolder(File directory, InputStyle inputStyle, OutputStyle outputStyle) {
        if (!directory.isDirectory())
            throw new UnsupportedOperationException("File is not a directory.");

        for (File f : Objects.requireNonNull(directory.listFiles())) {
            if (f.getName().equals(outputStyle.name())) {
                return searchInOutputStyleFolder(f, inputStyle, outputStyle);
            }
        }
        return null;
    }

    private static List<Pair<String, List<TransitionInterface>>> searchInOutputStyleFolder(File directory, InputStyle inputStyle, OutputStyle outputStyle) {
        if (!directory.isDirectory())
            throw new UnsupportedOperationException("File is not a directory.");
        List<Pair<String, List<TransitionInterface>>> solutions = new ArrayList<>();

        for (File f : Objects.requireNonNull(directory.listFiles())) {

            /*************************************************************************/
            if (f.getName().equals("0000") || f.getName().equals("0001") || f.getName().equals("0002") ||
                    f.getName().equals("0003") || f.getName().equals("0004") || f.getName().equals("0005") ||
                    f.getName().equals("0006") || f.getName().equals("0007") || f.getName().equals("0008"))
                continue;
            /*************************************************************************/


            solutions.add(new Pair<>(f.getName(), searchInIterationsFolder(f, inputStyle, outputStyle)));
        }
        return solutions;
    }

    private static List<TransitionInterface> searchInIterationsFolder(File directory, InputStyle inputStyle, OutputStyle outputStyle) {
        File bestDir = new File(directory.getPath() + "/Best");
        if (!directory.isDirectory())
            throw new UnsupportedOperationException("File is not a directory.");
        String[] names = bestDir.list();
        if (names == null)
            return null;
        Arrays.sort(names);
        List<TransitionInterface> result = new ArrayList<>();
        if (names.length < 90)
            for (int i = 0; i < names.length; i++) {
                File f = new File(bestDir.getPath() + "/" + names[i]);
                result.add(TransitionBuilder.build(FetchFile.lines(f)));
            }
        else {
            for (int i = 0; i < names.length; i += 10) {
                File f = new File(bestDir.getPath() + "/" + names[i]);
                result.add(TransitionBuilder.build(FetchFile.lines(f)));
            }
        }
        return result;
    }
}
