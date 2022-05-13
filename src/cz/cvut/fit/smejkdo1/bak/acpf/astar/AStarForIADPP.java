package cz.cvut.fit.smejkdo1.bak.acpf.astar;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.AgentCommunicator;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Version for use in Interruptible Asynchronous Decentralized Prioritized Planning algorithm.
 */
public class AStarForIADPP extends AStar implements Runnable {
    private final Pos start;
    private final Pos end;
    private final GameMap gameMap;
    private final Map<Pos, List<Pos>> constraints;
    private static final int DIST_OF_OPPONENT_CONSIDERATION = 2;
    private final Agent agent;
    public boolean stopThread = false;
    private List<Pos> result;
    private final Team team;
    private final ReverseResumableAStar heuristicAStar;

    public AStarForIADPP(Map<Pos, List<Pos>> constraints, Agent agent, GameMap gameMap) {
        this.start = agent.getPos();
        this.end = agent.getTarget();
        this.gameMap = gameMap;
        this.constraints = constraints;
        this.team = agent.getTeam();
        this.agent = agent;
        this.heuristicAStar = agent.getaStar();
    }

    private List<Pos> findPathWithConstrains() {
        if (stopThread)
            return null;
        return findPathWithConstrains(start, end, gameMap, constraints, team);
    }

    private Pair<List<Set<Pos>>, Map<Pair<Pos, Integer>, Pos>> prepareConstraints() {
        List<Set<Pos>> constraintSets = new ArrayList<>();
        Map<Pair<Pos, Integer>, Pos> antiSwap = new HashMap<>();
        if (constraints != null && !constraints.isEmpty()) {
            synchronized (constraints) {
                OptionalInt opt = constraints.values().stream().mapToInt(List::size).max();
                int bound = 0;
                if (opt.isPresent())
                    bound = opt.getAsInt();
                for (int i = 0; i < bound; i++) {

                    if (stopThread)
                        return null;

                    int finalI = i;
                    constraintSets.add(constraints.values()
                            .stream().map(list -> {
                                if (list.isEmpty())
                                    return null;
                                if (finalI < list.size())
                                    return list.get(list.size() - 1 - finalI);
                                return list.get(0);
                            }).filter(Objects::nonNull).collect(Collectors.toSet()));
                    if (i > 0) {
                        constraints.keySet().forEach(pos -> {
                            if (constraints.get(pos).size() > finalI)
                                antiSwap.put(new Pair<>(
                                                constraints.get(pos)
                                                        .get(constraints.get(pos).size() - 1 - finalI),
                                                finalI),
                                        constraints.get(pos)
                                                .get(constraints.get(pos).size() - finalI));
                        });
                    } else {
                        constraints.keySet().forEach(pos -> {
                            if (!constraints.get(pos).isEmpty())
                                antiSwap.put(new Pair<>(
                                        constraints.get(pos)
                                                .get(constraints.get(pos).size() - 1),
                                        finalI), pos);
                        });
                    }
                }
            }
        }
        return new Pair<>(constraintSets, antiSwap);
    }

    public List<Pos> findPathWithConstrains(Pos start, Pos end, GameMap gameMap,
                                            Map<Pos, List<Pos>> constraints, Team team) {
        AStarQueue<Pos> open = new AStarQueue();
        HashSet<Pos> closed = new HashSet<>();
        prev = new HashMap<>();
        Map<Pos, Integer> dist = new HashMap<>();
        Pos current;

        if (stopThread)
            return null;

        open.add(start, 0);
        dist.put(start, 0);

        if (stopThread)
            return null;
        Pair<List<Set<Pos>>, Map<Pair<Pos, Integer>, Pos>> pair = prepareConstraints();
        if (pair == null)
            return null;
        List<Set<Pos>> constraintSets = pair.getKey();
        Map<Pair<Pos, Integer>, Pos> antiSwap = pair.getValue();


        while (!open.empty()) {


            if (stopThread)
                return null;

            current = open.pop();
            if (current.equals(end)) {
                return build(current);
            }

            for (Pos pos : getVicinity(current, dist.get(current), team)) {

                if (stopThread)
                    return null;

                if (closed.contains(pos))
                    continue;
                int length = dist.get(current) + 1;
                if (blockedByConstrains(constraintSets, antiSwap, pos, length, current))
                    continue;

                if (stopThread)
                    return null;

                if (open.contains(pos)) {
                    if (length < dist.get(pos)) {
                        dist.remove(pos);
                        dist.put(pos, length);
                        prev.remove(pos);
                        prev.put(pos, current);
                    }
                } else {
                    int heuristic = heuristicAStar.synchronizedAbstractDist(pos);
                    if (heuristic == Integer.MAX_VALUE) {
                        open.add(pos, Integer.MAX_VALUE);
                    } else {
                        open.add(pos, length + 2 * heuristic);
                    }
                    dist.put(pos, length);
                    prev.put(pos, current);
                }
            }
            closed.add(current);
        }
        return null;
    }

    private boolean blockedByConstrains(List<Set<Pos>> constraintSets,
                                        Map<Pair<Pos, Integer>, Pos> antiSwap,
                                        Pos pos, int length, Pos current) {
        boolean positionalConstraints = (!constraintSets.isEmpty()) && (length < constraintSets.size() ?
                constraintSets.get(length - 1).contains(pos)
                : constraintSets.get(constraintSets.size() - 1).contains(pos));
        Pair<Pos, Integer> pair = length - 1 < constraintSets.size() ?
                new Pair<>(current, length - 1) : new Pair<>(current, constraintSets.size() - 1);
        boolean swapConstraints = antiSwap.containsKey(pair) && antiSwap.get(pair).equals(pos);
        return positionalConstraints || swapConstraints;
    }

    private List<Pos> getVicinity(Pos pos, int distance, Team team) {
        List<Pos> result;
        if (distance > DIST_OF_OPPONENT_CONSIDERATION)
            result = gameMap.neighbors(pos);
        else
            result = gameMap.neighbors(pos, team);
        //result.add(pos);
        return result;
    }

    @Override
    public void run() {
        result = findPathWithConstrains();
        if (!stopThread) {
            List<AgentCommunicator> communicators = gameMap
                    .getLowerPriorityAgentCommunicators(agent.getPriority(), agent.getTeam());
            if (communicators != null && !communicators.isEmpty())
                communicators.forEach(communicator -> communicator.sendPath(result, agent.getPos()));

        }
        AgentCommunicator.getGlobalTermination(gameMap).markAsTerminated(agent.getId());
    }

    public List<Pos> getResult() {
        return result;
    }
}
