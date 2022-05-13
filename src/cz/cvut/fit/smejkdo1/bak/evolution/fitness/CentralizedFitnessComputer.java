package cz.cvut.fit.smejkdo1.bak.evolution.fitness;

import cz.cvut.fit.smejkdo1.bak.acpf.Acpf;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.MatchWinData;
import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationInterface;

import java.util.*;
import java.util.stream.Collectors;

public class CentralizedFitnessComputer {
    public static void compute(PopulationInterface population, List<Pair<GameMap, List<Pair<Pos, Pos>>>> gameMapData) {
        List<FSM> fsmList = population
                .getIndividuals().stream()
                .map(individual ->
                        new FSM(individual.getGenotype().getInputStyle(),
                                individual.getGenotype().getOutputStyle(),
                                individual.getGenotype())).collect(Collectors.toList());
        List<List<MatchWinData>> matchDataList = fsmList
                .parallelStream()
                .map(fsm -> computeForFSM(fsm, fsmList, gameMapData))
                .collect(Collectors.toList());
        process(matchDataList, population);
    }

    //Takes very long, if two genotypes are the same hashmap puts them in same value!!!
    private static void process(List<List<MatchWinData>> dataList, PopulationInterface population) {
        Map<TransitionInterface, List<MatchWinData>>  hashMap = new HashMap<>();

        dataList.forEach(row -> row.forEach(data -> {
            hashMap.putIfAbsent(data.getMyTransition(), new ArrayList<>());
            hashMap.putIfAbsent(data.getOpponentTransition(), new ArrayList<>());
            hashMap.get(data.getMyTransition()).add(data);
            hashMap.get(data.getOpponentTransition()).add(data.reverse());
        }));

        population.getIndividuals()
                .parallelStream()
                .forEach(individual ->
                        individual.processFitnessData(hashMap.get(individual.getGenotype())));
    }

    public static List<MatchWinData> computeForFSM(FSM fsm,
                                                   List<FSM> othersList,
                                                   List<Pair<GameMap, List<Pair<Pos, Pos>>>> mapsAndTargets){
        List<MatchWinData> result = Collections.synchronizedList(new ArrayList<>());

        othersList.parallelStream().forEach(oth ->
        {
            if (oth != fsm)
                result.add(Acpf.runAcpf(fsm, oth, mapsAndTargets));
        });
        return result;
    }

}
