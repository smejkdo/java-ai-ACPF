package cz.cvut.fit.smejkdo1.bak.acpf.map;


import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.AgentCommunicator;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.TeamConverter;
import cz.cvut.fit.smejkdo1.bak.acpf.game.GameTurn;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Node;
import cz.cvut.fit.smejkdo1.bak.acpf.node.NodeState;
import cz.cvut.fit.smejkdo1.bak.acpf.node.NodeStateConverter;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameMap {

    private static final Pos UP = new Pos(1, 0);
    private static final Pos DOWN = new Pos(-1, 0);
    private static final Pos RIGHT = new Pos(0, 1);
    private static final Pos LEFT = new Pos(0, -1);

    private Map<Pos, Node> map;
    private final Pos mapSize;
    private Map<Pos, Agent> agentPositions;
    private static AtomicInteger idCounter = new AtomicInteger(0);
    private final AtomicInteger id;

    private GameTurn thisTurn;

    public GameTurn getThisTurn() {
        return thisTurn;
    }

    public void setThisTurn(GameTurn thisTurn) {
        this.thisTurn = thisTurn;
    }

    public GameMap(GameMap gameMap){
        Map<Pos, Node> newMap = new HashMap<>();
        for (Pos pos : gameMap.map.keySet()) {
            newMap.put(pos, gameMap.map.get(pos).deepCopy());
        }
        this.map = newMap;
        this.mapSize = gameMap.mapSize;
        this.agentPositions = new HashMap<>();
        this.id = new AtomicInteger(idCounter.getAndIncrement());
        map.values().forEach(Node::cleanUp);
    }

    public GameMap(Map<Pos, Node> map, Pos mapSize) {
        this.map = map;
        this.mapSize = mapSize;
        agentPositions = new HashMap<>();
        this.id = new AtomicInteger(idCounter.getAndIncrement());
    }

    public void reinitialize() {
        map.values().forEach(Node::cleanUp);
        agentPositions = new HashMap<>();
    }

    public Node getNode(Pos pos) {
        return map.get(pos);
    }

    public List<Pos> neighbors(Pos pos) {
        return neighbors(pos, false, false);
    }

    public List<Pos> neighbors(Pos pos, Team team) {
        if (team.equals(Team.RED))
            return neighbors(pos, false, true);
        else
            return neighbors(pos, true, false);
    }

    public List<Pos> neighbors(Pos pos, boolean passRed, boolean passBlu) {
        List<Pos> res = new ArrayList<>();

        if (isPosValid(pos.add(UP), passRed, passBlu)) {
            res.add(map.get(pos.add(UP)).getPos());
        }
        if (isPosValid(pos.add(DOWN), passRed, passBlu)) {
            res.add(map.get(pos.add(DOWN)).getPos());
        }
        if (isPosValid(pos.add(RIGHT), passRed, passBlu)) {
            res.add(map.get(pos.add(RIGHT)).getPos());
        }
        if (isPosValid(pos.add(LEFT), passRed, passBlu)) {
            res.add(map.get(pos.add(LEFT)).getPos());
        }
        return res;
    }
    private boolean isPosValid (Pos pos, boolean passRed, boolean passBlu){
        if (pos.x < 0 || pos.y < 0 || pos.x >= mapSize.x || pos.y >= mapSize.y
                || map.get(pos).getType() == NodeState.WALL
                || (agentPositions.get(pos) != null
                && agentPositions.get(pos).isOnTarget()))
            return false;
        if (passRed && map.get(pos).getTeam() == Team.RED)
            return false;
        return !passBlu || map.get(pos).getTeam() != Team.BLU;
    }
    
    public void print(){

        for (int i = 0; i < mapSize.x; i++) {
            for (int j = 0; j < mapSize.y; j++) {
                    if (map.get(new Pos(i, j)).getTeam().equals(Team.NONE))
                        System.out.print(NodeStateConverter.toString(map.get(new Pos(i, j)).getType()));
                    else
                        System.out.print(TeamConverter.toString(map.get(new Pos(i, j)).getTeam()));
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    public Map<Pos, Node> getMap() {
        return map;
    }

    public Pos getMapSize() {
        return mapSize;
    }

    public int getId() {
        return id.get();
    }

    public List<Boolean> toBinary() {
        return new ArrayList<>(mapSize.toBinary());
    }

    public void clearNode(Pos pos) {
        agentPositions.remove(pos);
        map.get(pos).setTeam(Team.NONE);
    }

    public void updatePosition(Pos pos, Agent agent) {
        agentPositions.put(pos, agent);
        try {
            map.get(pos).setTeam(agent.getTeam());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<Pos> getAgentPositionsSet() {
        return agentPositions.keySet();
    }

    @Deprecated
    public List<List<Integer>> getProximity(Agent agent, int size) {
        List<List<Integer>> list = new ArrayList<>();
        int bound = (size - 1) / 2;
        Pos pos;
        for (int i = agent.getPos().x - bound; i <= agent.getPos().x + bound; i++) {
            list.add(new ArrayList<>());
            for (int j = agent.getPos().y - bound; j <= agent.getPos().y + bound; j++) {
                pos = new Pos(i, j);
                if (agent.getPos().equals(pos))
                    continue;
                if (getNode(pos).getTeam().equals(Team.NONE)){
                    if (agent.getFastPath().get(agent.getFastPath().size() - 1).equals(pos)) {
                        list.get(list.size() - 1).add(5);
                        continue;
                    }
                    switch (getNode(pos).getType()){
                        case EMPTY: list.get(list.size() - 1).add(0); continue;
                        case WALL: list.get(list.size() - 1).add(1); continue;
                        case TARGET: list.get(list.size() - 1).add(2);
                    }
                } else {
                    if (agent.getTeam().equals(getNode(pos).getTeam()))
                        list.get(list.size() - 1).add(3);
                    else
                        list.get(list.size() - 1).add(4);
                }
            }
        }
        return list;
    }

    public GameMap deepCopy() {
        return new GameMap(this);
    }


    /**
     * Creates horizontally mirrored copy.
     *
     * @return new game map
     */
    public GameMap mirrorMapHorizontally() {
        Map<Pos, Node> mirrored = new HashMap<>();
        for (int i = 0; i < this.mapSize.x; i++) {
            for (int j = 0; j < this.mapSize.y; j++) {
                Pos newPos = new Pos(this.mapSize.x - 1 - i, j);
                mirrored.put(newPos, new Node(newPos, this.getNode(new Pos(i, j)).getType()));
            }
        }
        return new GameMap(mirrored, this.mapSize);
    }

    /**
     * Creates vertically mirrored copy.
     *
     * @return new game map
     */
    public GameMap mirrorMapVertically() {
        Map<Pos, Node> mirrored = new HashMap<>();
        for (int i = 0; i < this.mapSize.x; i++) {
            for (int j = 0; j < this.mapSize.y; j++) {
                Pos newPos = new Pos(i, this.mapSize.y - 1 - j);
                mirrored.put(newPos, new Node(newPos, this.getNode(new Pos(i, j)).getType()));
            }
        }
        return new GameMap(mirrored, this.mapSize);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameMap)) return false;
        GameMap gameMap = (GameMap) o;
        return map.equals(gameMap.map) &&
                mapSize.equals(gameMap.mapSize);
    }

    public int agentPriorityOnPos(Pos pos) {
        return agentPositions.get(pos).getPriority();
    }

    @Override
    public int hashCode() {
        return Objects.hash(map, mapSize);
    }

    public List<AgentCommunicator> getLowerPriorityAgentCommunicators(int priority, Team team) {
        List<Agent> agents = thisTurn.getAgents(team);
        List<AgentCommunicator> result = new ArrayList<>();
        for (Agent agent :
                agents) {
            if (agent.getPriority() < priority)
                result.add(agent.getCommunicator());
        }
        return result;
    }

    public int getActiveTeamSize(Team team) {
        return thisTurn.getAgents(team).size();
    }
}
