package cz.cvut.fit.smejkdo1.bak.inspection;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.map.LoadMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.MatchWinData;
import cz.cvut.fit.smejkdo1.bak.evolution.fitness.SimpleFitnessComputer;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.Individual;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.IndividualInterface;
import cz.cvut.fit.smejkdo1.bak.evolution.population.Population;
import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationInterface;

import java.util.ArrayList;
import java.util.List;

public class EvaluationData {

    private final List<MatchWinData> fitness;

    public EvaluationData(List<MatchWinData> fitness) {
        this.fitness = fitness;
    }

    public static EvaluationData evaluate(List<TransitionInterface> transitionInterfaces) {
        List<IndividualInterface> inds = new ArrayList<>();
        for (TransitionInterface transition : transitionInterfaces) {
            inds.add(new Individual(transition));
        }
        PopulationInterface population = new Population();
        population.setIndividuals(inds);
        List<Pair<GameMap, List<Pair<Pos, Pos>>>> mapsAndTargets = LoadMap.fetchAllTestMapsData();
        SimpleFitnessComputer.compute(population, mapsAndTargets);
        List<MatchWinData> fitness = new ArrayList<>();
        for (IndividualInterface ind :
                inds) {
            fitness.add(ind.getFitness());
        }

        return new EvaluationData(fitness);
    }

    private static String header() {
        return "Win;Loss;Tie;AgentsOnTarget;OpponentsAgentsOnTarget;L1;L2;L3\n";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(header());
        for (MatchWinData mwd : fitness) {
            mwd.toCSV(sb);
        }
        return sb.toString();
    }
}
