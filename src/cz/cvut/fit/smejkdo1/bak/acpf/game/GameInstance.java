package cz.cvut.fit.smejkdo1.bak.acpf.game;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.AgentBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.map.LoadMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.target.TargetFinder;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.io.File;
import java.util.List;

import static cz.cvut.fit.smejkdo1.bak.acpf.Main.println;

public class GameInstance implements Runnable {
    private GameTurn gameTurn;

    public GameInstance(int mapNumber, FSM redFM, FSM bluFM) {
        this.gameTurn = GameInstance.createGameInstance(mapNumber, redFM, bluFM);
    }

    public GameInstance(String mapName, int scenario, FSM redFM, FSM bluFM) {
        this.gameTurn = GameInstance.createGameInstance(mapName, scenario, redFM, bluFM);
    }

    public GameInstance() {

    }

    /**
     * Initializes game based on arguments
     *
     * @param mapNumber number of requested map to load
     * @param redFM     FM for red team
     * @param bluFM     FM for blu team
     * @return prepared initialized GameTurn
     */
    public static GameTurn createGameInstance(int mapNumber, FSM redFM, FSM bluFM) {
        GameMap gameMap;
        gameMap = LoadMap.fetchMyMap(mapNumber);
        List<Pair<Pos, Pos>> agentPositions = TargetFinder.fetchAllTargetsForMyMap(mapNumber);

        List<Agent> redAgents;
        List<Agent> bluAgents;
        Pair<List<Agent>, List<Agent>> agents = AgentBuilder.buildAll(agentPositions, redFM, bluFM, gameMap);
        redAgents = agents.getKey();
        bluAgents = agents.getValue();
        return new GameTurn(gameMap, redAgents, bluAgents);
    }

    public static GameTurn createGameInstance(String mapName, int scenario, FSM redFM, FSM bluFM) {
        Pair<GameMap, List<Pair<Pos, Pos>>> gameData;
        gameData = LoadMap.fetchMovingaiMapData(mapName, scenario);
        GameMap gameMap = gameData.getKey();

        List<Agent> redAgents;
        List<Agent> bluAgents;
        Pair<List<Agent>, List<Agent>> agents = AgentBuilder.buildAll(gameData.getValue(), redFM, bluFM, gameMap);
        redAgents = agents.getKey();
        bluAgents = agents.getValue();
        return new GameTurn(gameMap, redAgents, bluAgents);
    }

    /**
     * Initializes game based on arguments
     *
     * @param mapNumber number of requested map to load
     * @param redFM     path to saved FM inside resources/FiniteAutomatons/ for red team
     * @param bluFM     path to saved FM inside resources/FiniteAutomatons/ for blu team
     * @return prepared initialized GameTurn
     */
    public static GameTurn createGameInstance(int mapNumber, String redFM, String bluFM) {
        TransitionInterface t1 = TransitionBuilder.build(
                FetchFile
                        .lines("resources/FiniteAutomatons/" + redFM));
        TransitionInterface t2 = TransitionBuilder.build(
                FetchFile
                        .lines("resources/FiniteAutomatons/" + bluFM));

        FSM f1 = new FSM(t1);
        FSM f2 = new FSM(t2);

        return createGameInstance(mapNumber, f1, f2);
    }

    public static GameTurn createGameInstance(String mapName, int scenario, String redFM, String bluFM) {
        TransitionInterface t1 = TransitionBuilder.build(
                FetchFile
                        .lines("resources/FiniteAutomatons/" + redFM));
        TransitionInterface t2 = TransitionBuilder.build(
                FetchFile
                        .lines("resources/FiniteAutomatons/" + bluFM));

        FSM f1 = new FSM(t1);
        FSM f2 = new FSM(t2);

        return createGameInstance(mapName, scenario, f1, f2);
    }

    public static GameTurn createGameInstance(int mapNumber, File redFM, File bluFM) {
        TransitionInterface t1 = TransitionBuilder.build(
                FetchFile.lines(redFM));
        TransitionInterface t2 = TransitionBuilder.build(
                FetchFile.lines(bluFM));

        FSM f1 = new FSM(t1);
        FSM f2 = new FSM(t2);

        return createGameInstance(mapNumber, f1, f2);
    }

    public static GameTurn createGameInstance(String mapName, int scenario, File redFM, File bluFM) {
        TransitionInterface t1 = TransitionBuilder.build(
                FetchFile.lines(redFM));
        TransitionInterface t2 = TransitionBuilder.build(
                FetchFile.lines(bluFM));

        FSM f1 = new FSM(t1);
        FSM f2 = new FSM(t2);

        return createGameInstance(mapName, scenario, f1, f2);
    }

    @Override
    public void run() {
        gameTurn.getGameMap().print();
        do {
            gameTurn.makeTurn();
            gameTurn.getGameMap().print();
        } while (!gameTurn.isGameFinished());
        println(gameTurn.getGameWinData());
    }


}
