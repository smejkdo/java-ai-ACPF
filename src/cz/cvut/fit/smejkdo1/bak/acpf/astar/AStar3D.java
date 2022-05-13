package cz.cvut.fit.smejkdo1.bak.acpf.astar;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.AgentCommunicator;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos3D;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class AStar3D implements Runnable {
    private static final int MAX_TIME = 16;
    private static final int DIST_OF_OPPONENT_CONSIDERATION = 2;
    private final Pos start;
    private final Pos target;
    private final GameMap gameMap;
    private final Agent agent;
    private final Team team;
    private final ReverseResumableAStar heuristicAStar;
    private final Map<Pos, List<Pos>> constraints;
    private final AStarQueue<Pos3D> open = new AStarQueue<>();
    private final HashSet<Pos3D> closed = new HashSet<>();
    private final Map<Pos, Integer> dist = new HashMap<>();
    public boolean stopThread = false;
    protected Map<Pos3D, Pos3D> prev = new HashMap<>();
    private List<Pos> result;
    private Pos3D end;
    //private final List<Pos> oldResult;

    public AStar3D(Map<Pos, List<Pos>> constraints, Agent agent, GameMap gameMap/*, List<Pos> oldResult*/) {
        this.start = agent.getPos();
        this.target = agent.getTarget();
        this.gameMap = gameMap;
        this.constraints = constraints;
        this.team = agent.getTeam();
        this.agent = agent;
        this.heuristicAStar = agent.getaStar();
        //this.oldResult = oldResult;

        dist.put(start, 0);
        open.add(new Pos3D(start, 0), heuristicAStar.synchronizedAbstractDist(start));
    }


    private boolean findPath() {
        if (stopThread)
            return false;
        Pair<Set<Pos3D>, Map<Pos3D, Pos3D>> pair = prepareConstraints();
        if (pair == null)
            return false;
        Set<Pos3D> constraintSets = pair.getKey();
        Map<Pos3D, Pos3D> antiSwap = pair.getValue();

        while (!open.empty()) {

            if (stopThread)
                return false;

            Pos3D current = open.pop();
            closed.add(current);

            if (stopThread)
                return false;

            if (current.to2D().equals(target) || current.time == MAX_TIME) {
                end = current;
                return true;
            }

            if (stopThread)
                return false;

            for (Pos3D neighbor : getVicinity(current, team)) {
                if (stopThread)
                    return false;
                if (closed.contains(neighbor) || blockedByConstrains(constraintSets, antiSwap, neighbor, current))
                    continue;
                if (!open.contains(new Pos3D(neighbor, current.time + 1)) && !closed.contains(neighbor)) {
                    int heuristic = heuristicAStar.synchronizedAbstractDist(neighbor.to2D());
                    if (heuristic == Integer.MAX_VALUE)
                        open.add(neighbor, heuristic);
                    else
                        open.add(neighbor, neighbor.time + 2 * heuristic);
                    dist.put(neighbor.to2D(), neighbor.time);
                    prev.put(neighbor, current);
                }
                if (stopThread)
                    return false;
                if (dist.containsKey(neighbor.to2D()) && (dist.get(neighbor.to2D()) > neighbor.time && checkPassing(neighbor, dist.get(neighbor.to2D()), constraintSets))) {
                    dist.put(neighbor.to2D(), neighbor.time);
                    int heuristic = heuristicAStar.synchronizedAbstractDist(neighbor.to2D());
                    if (heuristic == Integer.MAX_VALUE)
                        open.update(neighbor, heuristic);
                    else
                        open.update(neighbor, neighbor.time + 2 * heuristic);
                    prev.put(neighbor, current);
                }
            }
        }
        return false;

    }

    private boolean checkPassing(Pos3D pos, Integer t, Set<Pos3D> constraintSets) {
        Pos3D tmp = pos;
        while (tmp.time <= t) {
            tmp = tmp.increasedTime(1);
            if (constraintSets.contains(tmp))
                return false;
        }
        return true;
    }

    private List<Pos3D> getVicinity(Pos3D pos, Team team) {
        List<Pos> result;
        if (pos.time > DIST_OF_OPPONENT_CONSIDERATION)
            result = gameMap.neighbors(pos);
        else
            result = gameMap.neighbors(pos, team);
        result.add(pos);
        return result.stream().map(p -> new Pos3D(p, pos.time + 1)).collect(Collectors.toList());
    }

    private boolean blockedByConstrains(Set<Pos3D> constraintSets,
                                        Map<Pos3D, Pos3D> antiSwap,
                                        Pos3D neighbor, Pos3D current) {
        boolean positionalConstraints = (!constraintSets.isEmpty()) && constraintSets.contains(neighbor);
        boolean swapConstraints = antiSwap.containsKey(current.increasedTime(1))
                && antiSwap.get(current.increasedTime(1)).equals(neighbor.decreasedTime(1));
        return positionalConstraints || swapConstraints;
    }


    private Pair<Set<Pos3D>, Map<Pos3D, Pos3D>> prepareConstraints() {
        Set<Pos3D> constraintSets = new HashSet<>();
        Map<Pos3D, Pos3D> antiSwap = new HashMap<>();
        if (stopThread)
            return null;

        if (constraints != null && !constraints.isEmpty()) {
            synchronized (constraints) {
                for (Pos pos : constraints.keySet()) {
                    if (constraints.get(pos) == null || constraints.get(pos).isEmpty())
                        continue;
                    int size = constraints.get(pos).size();
                    for (int i = MAX_TIME; i >= 0; i--) {
                        if (stopThread)
                            return null;
                        if (i < size)
                            constraintSets.add(new Pos3D(constraints.get(pos).get(i), size - i));
                        else
                            constraintSets.add(new Pos3D(constraints.get(pos).get(0), i));
                        if (i > 0) {
                            if (i < size)
                                antiSwap.put(new Pos3D(
                                                constraints.get(pos)
                                                        .get(constraints.get(pos).size() - 1 - i), i + 1),
                                        new Pos3D(constraints.get(pos)
                                                .get(constraints.get(pos).size() - i), i));
                        } else {
                            if (!constraints.get(pos).isEmpty())
                                antiSwap.put(new Pos3D(
                                                constraints.get(pos)
                                                        .get(constraints.get(pos).size() - 1), 1),
                                        new Pos3D(pos, 0));
                        }
                    }
                }

            }
        }
        return new Pair<>(constraintSets, antiSwap);
    }

    private List<Pos> build() {
        List<Pos> lst = new ArrayList<>();
        List<Pos> tmp = heuristicAStar.synchronizedGetPath(end.to2D());
        if (tmp != null)
            lst.addAll(tmp);
        lst.add(end.to2D());
        int time = end.time;

        while (prev.containsKey(lst.get(lst.size() - 1).as3D(time))) {
            lst.add(prev.get(lst.get(lst.size() - 1).as3D(time)).to2D());
            time--;
        }
        lst.remove(lst.size() - 1);
        return lst;
    }


    @Override
    public void run() {
        if (findPath())
            result = build();
        /*else if (!stopThread && heuristicAStar.synchronizedAbstractDist(start) != Integer.MAX_VALUE)
            result = heuristicAStar.synchronizedGetPath(start);*/
        if (!stopThread && (result == null || !result.equals(agent.getFastPath()))) {
            List<AgentCommunicator> communicators = gameMap
                    .getLowerPriorityAgentCommunicators(agent.getPriority(), agent.getTeam());
            if (communicators != null && !communicators.isEmpty())
                communicators.forEach(communicator -> communicator.sendPath(result, agent.getPos()));
            agent.setFastPath(result);
        }
        AgentCommunicator.getGlobalTermination(gameMap).markAsTerminated(agent.getId());
    }


    public List<Pos> getResult() {
        return result;
    }
}
