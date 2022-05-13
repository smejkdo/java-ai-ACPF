package cz.cvut.fit.smejkdo1.bak.acpf.astar;


import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;

import java.util.*;

public class AStar {
    protected Map<Pos, Pos> prev;
    protected Set<Pos> upcomingPositions;

    protected List<Pos> build(Pos end) {
        List<Pos> lst = new ArrayList<>();
        lst.add(end);
        while (prev.containsKey(lst.get(lst.size() - 1))) {
            lst.add(prev.get(lst.get(lst.size() - 1)));
        }
        lst.remove(lst.size() - 1);
        return lst;
    }

    public List<Pos> findPath(Pos start, Pos end, GameMap gameMap) {
        return findPath(start, end, gameMap, false, false);
    }

    public List<Pos> findPath(Pos start, Pos end, GameMap gameMap, Set<Pos> upcomingPositions, Team team) {
        try {
            this.upcomingPositions = upcomingPositions;
            if (team.equals(Team.RED))
                return findPath(start, end, gameMap, false, true);
            else
                return findPath(start, end, gameMap, true, false);
        } finally {
            this.upcomingPositions = null;
        }
    }

    public List<Pos> findPath(Pos start, Pos end, GameMap gameMap, boolean passRed, boolean passBlu) {
        AStarQueue<Pos> open = new AStarQueue<>();
        HashSet<Pos> closed = new HashSet<>();
        prev = new HashMap<>();
        Map<Pos, Integer> dist = new HashMap<>();
        Pos current;
        int length;

        open.add(start, 0);
        dist.put(start, 0);

        while (!open.empty()) {
            current = open.pop();
            if (current.equals(end)) {
                return build(current);
            }

            for (Pos pos : gameMap.neighbors(current, passRed, passBlu)) {
                if (closed.contains(pos)
                        || (upcomingPositions != null && !upcomingPositions.isEmpty()
                        && upcomingPositions.contains(pos))) {
                    continue;
                }

                length = dist.get(current) + ManhattanDistance.distance(pos, current);
                if (open.contains(pos)) {
                    if (length < dist.get(pos)) {
                        dist.remove(pos);
                        dist.put(pos, length);
                        prev.remove(pos);
                        prev.put(pos, current);

                    }
                } else {
                    open.add(pos, length + 2 * ManhattanDistance.distance(pos, end));
                    dist.put(pos, length);
                    prev.put(pos, current);
                }
            }
            closed.add(current);
        }
        return null;
    }
}
