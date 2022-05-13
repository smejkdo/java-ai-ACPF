package cz.cvut.fit.smejkdo1.bak.acpf.game;


import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.AgentCommunicator;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.util.WinCondition;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.GameWinData;

import java.util.*;

public class GameTurn {
    private final GameMap gameMap;
    private final List<Agent> redAgents;
    private final List<Agent> bluAgents;
    private Map<Pos, List<Agent>> reservedPositions;
    private List<Pos> collisions;

    private int turnCounter;
    private final int turnCount;
    private GameWinData gameWinData;
    private final boolean redComunicating;
    private final boolean bluComunicating;

    public static final List<Double> time = new ArrayList<>();

    public GameTurn(GameMap gameMap, List<Agent> redAgents, List<Agent> bluAgents) {
        this(gameMap, redAgents, bluAgents, 200);
    }

    public GameTurn(GameMap gameMap, List<Agent> redAgents, List<Agent> bluAgents, int turnCount) {
        this.gameMap = gameMap;
        this.gameMap.setThisTurn(this);
        this.redAgents = redAgents;
        this.bluAgents = bluAgents;
        this.turnCounter = 0;
        this.turnCount = turnCount;
        this.gameWinData = new GameWinData();
        this.gameWinData.setWinner(Team.NONE);
        this.redComunicating = !redAgents.isEmpty() && redAgents.get(0).isCommunicating();
        this.bluComunicating = !bluAgents.isEmpty() && bluAgents.get(0).isCommunicating();
    }

    public void makeTurn() {
        if (redComunicating)
            AgentCommunicator.initializeGlobalTerminator(gameMap, Team.RED);
        prepareRedMoves();
        if (redComunicating)
            AgentCommunicator.deleteGlobalTerminator(gameMap);
        checkCollisions();
        checkPosSwap(redAgents);
        updateRedAgents();

        if (bluComunicating)
            AgentCommunicator.initializeGlobalTerminator(gameMap, Team.BLU);
        prepareBluMoves();
        if (bluComunicating)
            AgentCommunicator.deleteGlobalTerminator(gameMap);
        checkCollisions();
        checkPosSwap(bluAgents);
        updateBluAgents();

        turnCounter++;
    }

    private void agentsTakeTurn(List<Agent> agents) {
        ThreadGroup tg = new ThreadGroup(agents.get(0).getTeam().toString() + gameMap.getId());
        List<Thread> threads = new ArrayList<>();
        for (Agent a : agents) {
            Thread t = new Thread(tg, a::takeTurn);
            threads.add(t);
        }
        threads.forEach(Thread::start);
        try {
            for (Thread thread : threads) thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareRedMoves() {
        reservedPositions = new HashMap<>();
        collisions = new ArrayList<>();
        Pos newPos;

        //double start, end;
        //start = System.currentTimeMillis();

        if (redComunicating)
            agentsTakeTurn(redAgents);
        else
            redAgents.parallelStream().forEach(Agent::takeTurn);

        //end = System.currentTimeMillis();

        //time.add(end - start);

        for (Agent redAgent : redAgents) {
            newPos = redAgent.getUpcomingPos();

            if (gameMap.getNode(newPos).getTeam().equals(Team.BLU)) {
                redAgent.resetMove();
                newPos = redAgent.getUpcomingPos();
            }
            if (!reservedPositions.containsKey(newPos))
                reservedPositions.put(newPos, new ArrayList<>());
            else if (reservedPositions.get(newPos).size() == 1)
                collisions.add(newPos);
            reservedPositions.get(newPos).add(redAgent);
        }
    }

    private void prepareBluMoves() {
        reservedPositions = new HashMap<>();
        collisions = new ArrayList<>();
        Pos newPos;
        if (bluComunicating)
            agentsTakeTurn(bluAgents);
        else
            bluAgents.parallelStream().forEach(Agent::takeTurn);
        for (Agent bluAgent : bluAgents) {
            newPos = bluAgent.getUpcomingPos();

            if (gameMap.getNode(newPos).getTeam().equals(Team.RED)) {
                bluAgent.resetMove();
                newPos = bluAgent.getUpcomingPos();
            }
            if (!reservedPositions.containsKey(newPos))
                reservedPositions.put(newPos, new ArrayList<>());
            else if (reservedPositions.get(newPos).size() == 1)
                collisions.add(newPos);
            reservedPositions.get(newPos).add(bluAgent);
        }
    }

    private void updateBluAgents() {
        bluAgents.forEach(Agent::clearNode);
        bluAgents.forEach(Agent::updatePosition);

    }
    private void updateRedAgents() {
        redAgents.forEach(Agent::clearNode);
        redAgents.forEach(Agent::updatePosition);
    }

    private void checkCollisions(){
        while (!collisions.isEmpty()) {
            Pos pos = collisions.get(collisions.size() - 1);
            List<Agent> agents = reservedPositions.get(pos);
            agents.parallelStream()
                    .forEach(Agent::resetMove);
            if (agents.parallelStream().noneMatch(a -> a.getPos() == pos))
                agents.get(0).setUpcomingPos(pos);

            reservedPositions.remove(pos);
            collisions.remove(collisions.size() - 1);

            for (Agent agent : agents) {
                Pos newPos = agent.getUpcomingPos();

                if (!reservedPositions.containsKey(newPos))
                    reservedPositions.put(newPos, new ArrayList<>());
                else if (reservedPositions.get(newPos).size() == 1)
                    collisions.add(newPos);
                reservedPositions.get(newPos).add(agent);
            }
        }
    }

    private void checkPosSwap(List<Agent> agents) {
        boolean rerunChecks = false;
        for (Agent a : agents) {
            if (!a.getPos().equals(a.getUpcomingPos())
                    && a.getTeam().equals(gameMap.getNode(a.getUpcomingPos()).getTeam())){
                if(swapCheck(a.getPos(), a.getUpcomingPos())) {
                    resetAgent(a);
                    rerunChecks = true;
                }
            }
        }
        if (rerunChecks){
            checkCollisions();
            checkPosSwap(agents);
        }
    }

    private boolean swapCheck(Pos initial, Pos goal){
        if (reservedPositions.containsKey(initial)) {
            Optional<Agent> a = reservedPositions.get(initial).stream().filter(agent -> agent.getPos().equals(goal)).findAny();
            if (a.isEmpty())
                return false;
            resetAgent(a.get());
            return true;
        }
        return false;
    }

    private void resetAgent(Agent a) {
        reservedPositions.get(a.getUpcomingPos()).remove(a);
        if (reservedPositions.get(a.getUpcomingPos()).size() <= 1){
            collisions.remove(a.getUpcomingPos());
            if (reservedPositions.get(a.getUpcomingPos()).size() == 0)
                reservedPositions.remove(a.getUpcomingPos());
        }

        a.resetMove();

        Pos newPos = a.getUpcomingPos();
        if (!reservedPositions.containsKey(newPos))
            reservedPositions.put(newPos, new ArrayList<>());
        else if (reservedPositions.get(newPos).size() == 1)
            collisions.add(newPos);
        reservedPositions.get(newPos).add(a);
    }


    public boolean isGameFinished() {
        if(areOnTarget(redAgents))
            gameWinData.setWinner(Team.RED);
        else if (areOnTarget(bluAgents))
            gameWinData.setWinner(Team.BLU);
        return !gameWinData.getWinner().equals(Team.NONE) || (areTurnsDepleted());
    }

    private boolean areTurnsDepleted() {
        return turnCounter == turnCount;
    }

    private boolean areOnTarget(List<Agent> agents) {
        for (Agent a : agents) {
            if(!a.isOnTarget())
                return false;
        }
        return true;
    }

    public GameWinData getGameWinData() {
        //System.out.println(time.stream().mapToDouble(a -> a).average());
        updateGameWinData();
        return gameWinData;
    }

    private void updateGameWinData() {
        if (!gameWinData.getWinner().equals(Team.NONE)) {
            gameWinData.setWinCondition(WinCondition.ALL_ON_TARGET);
            gameWinData.setScore(turnCount - turnCounter);
            if (gameWinData.getWinner().equals(Team.RED)) {
                gameWinData.setWinnerAgentsOnTargets(redAgents.size());
                gameWinData.setLooserAgentsOnTargets(bluAgents.size());
            } else {
                gameWinData.setWinnerAgentsOnTargets(bluAgents.size());
                gameWinData.setLooserAgentsOnTargets(redAgents.size());
            }
            return;
        }
        int redOnTarget = agentsOnTarget(redAgents);
        int bluOnTarget = agentsOnTarget(bluAgents);
        if (redOnTarget < bluOnTarget){
            gameWinData.setWinner(Team.BLU);
            gameWinData.setWinCondition(WinCondition.MORE_ON_TARGET);
            gameWinData.setScore(bluOnTarget - redOnTarget);
            gameWinData.setWinnerAgentsOnTargets(bluOnTarget);
            gameWinData.setLooserAgentsOnTargets(redOnTarget);
            return;
        } else if (redOnTarget > bluOnTarget){
            gameWinData.setWinner(Team.RED);
            gameWinData.setWinCondition(WinCondition.MORE_ON_TARGET);
            gameWinData.setScore(redOnTarget - bluOnTarget);
            gameWinData.setWinnerAgentsOnTargets(redOnTarget);
            gameWinData.setLooserAgentsOnTargets(bluOnTarget);
            return;
        }

        int redSum = sumOfDistances(redAgents);
        int bluSum = sumOfDistances(bluAgents);
        if (redSum > bluSum){
            gameWinData.setWinner(Team.BLU);
            gameWinData.setWinCondition(WinCondition.CLOSER_TO_TARGET);
            gameWinData.setScore(redSum - bluSum);
            gameWinData.setWinnerAgentsOnTargets(bluOnTarget);
            gameWinData.setLooserAgentsOnTargets(redOnTarget);
        } else if (redSum < bluSum){
            gameWinData.setWinner(Team.RED);
            gameWinData.setWinCondition(WinCondition.CLOSER_TO_TARGET);
            gameWinData.setScore(bluSum - redSum);
            gameWinData.setWinnerAgentsOnTargets(redOnTarget);
            gameWinData.setLooserAgentsOnTargets(bluOnTarget);
        } else {
            gameWinData.setWinner(Team.NONE);
            gameWinData.setWinCondition(WinCondition.DRAW);
            gameWinData.setScore(gameMap.getMapSize().x * gameMap.getMapSize().y - redSum);
            gameWinData.setWinnerAgentsOnTargets(redOnTarget);
            gameWinData.setLooserAgentsOnTargets(bluOnTarget);
        }
    }

    private int agentsOnTarget(List<Agent> agents) {
        return (int) agents.stream().filter(Agent::isOnTarget).count();
    }

    private int sumOfDistances(List<Agent> agents) {
        int sum = 0;
        for (Agent a : agents) {
            if (a.getDistanceToTarget() != Integer.MAX_VALUE)
                sum += a.getDistanceToTarget();
        }
        return sum;
    }

    public void reset() {
        gameMap.reinitialize();
        turnCounter = 0;
        reservedPositions = null;
        collisions = null;
        this.gameWinData = new GameWinData();
        this.gameWinData.setWinner(Team.NONE);
        redAgents.forEach(Agent::reinitialize);
        bluAgents.forEach(Agent::reinitialize);
    }

    public Pair<List<Agent>, List<Agent>> getAgents() {
        return new Pair<>(redAgents, bluAgents);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public List<Agent> getAgents(Team team) {
        if (team.equals(Team.RED))
            return redAgents;
        else if (team.equals(Team.BLU))
            return bluAgents;
        else return null;
    }
}
