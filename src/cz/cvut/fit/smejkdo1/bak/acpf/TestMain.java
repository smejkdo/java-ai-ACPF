package cz.cvut.fit.smejkdo1.bak.acpf;

import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.map.LoadMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.util.List;

/**
 * na rychle testy funkcnosti malych komponent.
 */
public class TestMain {
    /*public static void main(String[] args) {
        int populationSize = 200;
        for (InputStyle inputStyle :
                InputStyle.values()) {
            for (OutputStyle outputStyle :
                    OutputStyle.values()) {
                if (inputStyle.equals(InputStyle.COMPLEX_EIGHT_VICINITY) && outputStyle.equals(OutputStyle.TRANSITION_LIST))
                    continue;
                if (inputStyle.equals(InputStyle.INFO_EXCHANGE))
                    continue;
                if (inputStyle.equals(InputStyle.DATA_FROM_MAP))
                    continue;
                System.out.println(inputStyle + " with " + outputStyle);
                EvolutionInterface evolution = EvolutionBuilder
                        .build(
                                populationSize,
                                5,
                                5,
                                70,
                                100,
                                inputStyle, outputStyle);
                evolution.setEvolutionNumber("0004");
                evolution.setPopulation(PopulationBuilder.build(inputStyle + "/" + outputStyle + "/0002/LastGeneration/", populationSize));
                evolution.run();
            }
        }
    }*/
    /*public static void main(String[] args) {
        DecisionTrees dt = (DecisionTrees) TransitionBuilder.build(FetchFile
                .lines("resources/FiniteAutomatons/ANALYZED_EXAMPLE.wad"));
        IndividualInterface ind = new Individual(dt);
        FSM fsm = new FSM(dt);
        FSM aStarFsm = new FSM("ASTAR");
        FSM IADPPComparator = new FSM("IADPP");
        List<Pair<GameMap, List<Pair<Pos, Pos>>>> mapData = LoadMap.fetchAllTestMapsData();
        //List<MatchWinData> lst = SimpleFitnessComputer.computeForFSM(fsm, mapData);
        //ind.processFitnessData(lst);
        //System.out.println(ind.getFitness().toCSV());
        int i = 0;

        for (Pair<GameMap, List<Pair<Pos, Pos>>> data: mapData) {
            //data.getKey().print();
            System.out.println(i + " & Červený & " + Acpf.runMap(fsm, IADPPComparator, data).toAnalyze());
            System.out.println(i + " & Modrý & " + Acpf.runMap(IADPPComparator, fsm, data).toAnalyze());
            i++;
        }

    }*/

    public static void main(String[] args) {
        List<Pair<GameMap, List<Pair<Pos, Pos>>>> mapData = LoadMap.fetchAllTestMapsData();
        for (Pair<GameMap, List<Pair<Pos, Pos>>> data : mapData) {
            System.out.println(data.getKey().getMapSize() + " & " + data.getValue().size());
        }
    }
}
