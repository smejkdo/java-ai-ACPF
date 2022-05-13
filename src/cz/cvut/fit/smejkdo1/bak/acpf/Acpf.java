package cz.cvut.fit.smejkdo1.bak.acpf;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.AgentBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.game.GameTurn;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.map.LoadMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.GameWinData;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.MatchWinData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Acpf {
    public static MatchWinData runAcpf(TransitionInterface myTransition,
                                       TransitionInterface opponentTransition,
                                       InputStyle redInput,
                                       InputStyle bluInput) {
        FSM myFSM = new FSM(redInput, myTransition.getOutputStyle(), myTransition);
        FSM opponentFSM = new FSM(bluInput, opponentTransition.getOutputStyle(), opponentTransition);
        List<Pair<GameMap, List<Pair<Pos, Pos>>>> mapAndTarget = LoadMap.fetchAllEvolutionMapData();

        List<GameWinData> gameDataList = Collections.synchronizedList(new ArrayList<>());
        mapAndTarget.parallelStream()
                .forEach(pair ->
                        gameDataList.add(
                                runMap(myFSM,
                                        opponentFSM,
                                        pair.getKey(),
                                        pair.getValue())));
        mapAndTarget.parallelStream()
                .forEach(pair ->
                        gameDataList.add(
                                runMap(opponentFSM,
                                        myFSM,
                                        pair.getKey(),
                                        pair.getValue()).reverse()));
        return new MatchWinData(gameDataList);
    }

    public static GameWinData runMap(FSM redFSM,
                                     FSM bluFSM,
                                     Pair<GameMap, List<Pair<Pos, Pos>>> mapAndTargets) {
        return runMap(redFSM, bluFSM, mapAndTargets.getKey(), mapAndTargets.getValue());
    }

    public static GameWinData runMap(FSM redFSM,
                                     FSM bluFSM,
                                     GameMap gameMap,
                                     List<Pair<Pos, Pos>> targets) {
        GameMap gameMap1 = gameMap.deepCopy();
        Pair<List<Agent>, List<Agent>> agents = AgentBuilder.buildAll(targets, redFSM, bluFSM, gameMap1);
        GameTurn gameTurn = new GameTurn(gameMap1, agents.getKey(), agents.getValue());
        do {
            gameTurn.makeTurn();
        } while (!gameTurn.isGameFinished());
        return gameTurn.getGameWinData();
    }

    public static MatchWinData runAcpf(FSM myFSM,
                                       FSM opponentFSM,
                                       List<Pair<GameMap, List<Pair<Pos, Pos>>>> mapsAndTargets) {
        List<GameWinData> gameDataList;

        gameDataList = mapsAndTargets.stream()
                .map(pair -> runMap(myFSM,
                        opponentFSM,
                        pair)).collect(Collectors.toList());
        return new MatchWinData(gameDataList,
                myFSM.getTransition(),
                opponentFSM.getTransition());
    }
}
