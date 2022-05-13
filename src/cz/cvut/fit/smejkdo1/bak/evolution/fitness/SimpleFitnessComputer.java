package cz.cvut.fit.smejkdo1.bak.evolution.fitness;

import cz.cvut.fit.smejkdo1.bak.acpf.Acpf;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.MatchWinData;
import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleFitnessComputer {
    public static final int maxActiveComparators = 1;
    private static final FSM aStarFsm = new FSM("ASTAR");
    private static final FSM IADPPComparator = new FSM("IADPP");
    private static final List<FSM> comparators = SimpleFitnessComputer.LoadComparators();

    private static List<FSM> LoadComparators() {
        List<FSM> result = new ArrayList<>();
        String[] comparatorNames = FetchFile.fetchDirectory("resources/FiniteAutomatons/Best").list();
        if (comparatorNames == null)
            return null;
        for (String name : comparatorNames) {
            if (name.equals("BEST.wad"))
                continue;
            result.add(new FSM("Best/" + name.substring(0, name.length() - 4)));
        }
        return result;
    }

    public static void changeActiveComparators() {
        Collections.shuffle(comparators);
    }

    private static void process(List<List<MatchWinData>> dataList, PopulationInterface population) {

        population.getIndividuals().parallelStream().forEach(individual ->
                individual.processFitnessData(dataList.stream().filter(list ->
                        list.get(0).getMyTransition().equals(individual.getGenotype())).findFirst().get()));
    }

    public static void compute(PopulationInterface population, List<Pair<GameMap, List<Pair<Pos, Pos>>>> gameMapData) {
        List<FSM> fsmList = population
                .getIndividuals().stream()
                .map(individual ->
                        new FSM(individual.getGenotype().getInputStyle(),
                                individual.getGenotype().getOutputStyle(),
                                individual.getGenotype())).collect(Collectors.toList());
        List<List<MatchWinData>> matchDataList = fsmList
                .parallelStream()
                .map(fsm -> computeForFSM(fsm, gameMapData))
                .collect(Collectors.toList());
        process(matchDataList, population);
    }

    public static List<MatchWinData> computeForFSM (FSM fsm,
                                                    List<Pair<GameMap, List<Pair<Pos, Pos>>>> mapsAndTargets) {
        List<MatchWinData> result = Collections.synchronizedList(new ArrayList<>());
        result.add(Acpf.runAcpf(fsm, aStarFsm, mapsAndTargets));
        result.add(Acpf.runAcpf(aStarFsm, fsm, mapsAndTargets).reverse());
        //result.add(Acpf.runAcpf(fsm, IADPPComparator, mapsAndTargets));
        //result.add(Acpf.runAcpf(IADPPComparator, fsm, mapsAndTargets).reverse());
        /*if (comparators != null)
            for (FSM comparator :
                    comparators.subList(0, maxActiveComparators)) {
                result.add(Acpf.runAcpf(fsm, comparator, mapsAndTargets));
                result.add(Acpf.runAcpf(comparator, fsm, mapsAndTargets).reverse());
            }*/
        return result;
    }
}
