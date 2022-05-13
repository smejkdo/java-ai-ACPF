package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.FMInput;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;

import java.util.List;

public interface TransitionInterface {
    /**
     * transitions to a new state
     *
     * @param arg   input to finite machine to decide transition
     * @param state start state
     * @return number of new state
     */
    int transition(FMInput arg, int state);

    OutputStyle getOutputStyle();

    InputStyle getInputStyle();

    void mutateBit(int i);

    void mutate(int mutationRate);

    void fillRand(int mutationProbability, InputStyleUtils inputStyleUtils);

    void save(String fileName, String filePath);

    int getNumberOfBits();

    TransitionInterface deepCopy();

    List<TransitionInterface> crossover(TransitionInterface genotype);

    void repairSelf();

    int difference(TransitionInterface other);
    //int transitionRepresentation();

    void symmetry(int[] bits1, int[] bits2, int[] args1, int[] args2);
}
