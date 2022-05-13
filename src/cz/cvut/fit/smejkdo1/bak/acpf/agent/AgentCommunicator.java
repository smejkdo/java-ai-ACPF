package cz.cvut.fit.smejkdo1.bak.acpf.agent;

import cz.cvut.fit.smejkdo1.bak.acpf.astar.AStar3D;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentCommunicator {
    private static final Map<Integer, IADPPTermination> globalTermination = new HashMap<>();
    private final Map<Pos, List<Pos>> receivedPaths = Collections.synchronizedMap(new HashMap<>());
    private final Agent communicatorOwner;
    private AStar3D aStar;
    private Thread planning;
    //private List<Pos> oldResult;
    private final GameMap gameMap;

    public AgentCommunicator(Agent owner, GameMap gameMap) {
        this.communicatorOwner = owner;
        this.gameMap = gameMap;
    }

    public static synchronized void initializeGlobalTerminator(GameMap gameMap, Team team) {
        globalTermination.put(gameMap.getId(), new IADPPTermination(gameMap.getActiveTeamSize(team)));
    }

    public static synchronized void deleteGlobalTerminator(GameMap gameMap) {
        globalTermination.remove(gameMap.getId());
    }

    public static IADPPTermination getGlobalTermination(GameMap gameMap) {
        return globalTermination.get(gameMap.getId());
    }

    public void sendPath(List<Pos> path, Pos agentsPos) {
        globalTermination.get(gameMap.getId()).markAsRunning(communicatorOwner.getId());
        if (communicatorOwner.isOnTarget() || (path == null || (receivedPaths.containsKey(agentsPos)
                && path.equals(receivedPaths.get(agentsPos)))
                || path.isEmpty())) {
            if (path == null)
                receivedPaths.remove(agentsPos);
            globalTermination.get(gameMap.getId()).markAsTerminated(communicatorOwner.getId());
            return;
        }
        if (planning != null && planning.isAlive()) {
            aStar.stopThread = true;
        }
        synchronized (receivedPaths) {
            receivedPaths.put(agentsPos, Collections.synchronizedList(path));
            this.checkPlan();
        }
    }

    public synchronized void checkPlan() {
        /*if (aStar != null && aStar.getResult() != null)
            oldResult = aStar.getResult();*/
        aStar = new AStar3D(receivedPaths, communicatorOwner, gameMap/*, oldResult*/);
        planning = new Thread(aStar);
        planning.start();
    }

    public Thread getPlanning() {
        return planning;
    }

    public void updateAgentsPath() {
        communicatorOwner.setFastPath(aStar.getResult());
    }

    public List<Pos> getResult() {
        //if (aStar != null)
        return aStar.getResult();
        /*else
            return communicatorOwner.getFastPath();*/
    }

    public void newTurnUpdate() {
    }
}
