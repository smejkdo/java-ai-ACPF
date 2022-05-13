package cz.cvut.fit.smejkdo1.bak.evolution.individual;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.MatchWinData;

import java.util.List;

public interface IndividualInterface {
    void init(int mutationProbability, InputStyle inputStyle, OutputStyle outputStyle);

    void computeFitness(List<Pair<GameMap, List<Pair<Pos, Pos>>>> gameMapData);

    void processFitnessData(List<MatchWinData> data);

    void mutate(int mutationRate);

    List<IndividualInterface> crossover(IndividualInterface other);

    void repair();

    IndividualInterface deepCopy();

    String toString();

    int compare(IndividualInterface other);
    int dif (IndividualInterface other);
    boolean equals(IndividualInterface other);

    void setFitness(MatchWinData data);

    MatchWinData getFitness();
    TransitionInterface getGenotype();

}
