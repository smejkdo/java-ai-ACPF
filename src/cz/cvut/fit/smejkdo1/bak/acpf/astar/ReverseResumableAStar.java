package cz.cvut.fit.smejkdo1.bak.acpf.astar;

import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;

import java.util.*;

public class ReverseResumableAStar extends AStar {
    private final AStarQueue<Pos> open = new AStarQueue<>();
    private final HashSet<Pos> closed = new HashSet<>();
    private final Map<Pos, Integer> dist = new HashMap<>();
    private final GameMap gameMap;
    private final Pos start;
    private final Pos end;

    public ReverseResumableAStar(Pos start, Pos end, GameMap gameMap) {
        this.start = start;
        this.end = end;
        this.gameMap = gameMap;
        prev = new HashMap<>();
        dist.put(end, 0);
        open.add(end, ManhattanDistance.distance(end, start));
        resume(start);
    }

    private boolean resume(Pos target) {
        while (!open.empty()) {
            Pos p = open.pop();
            closed.add(p);
            if (p.equals(target))
                return true;
            for (Pos neighbor : gameMap.neighbors(p)) {
                int cost = dist.get(p) + 1;
                if (!open.contains(neighbor) && !closed.contains(neighbor)) {
                    open.add(neighbor, cost + ManhattanDistance.distance(neighbor, start));
                    dist.put(neighbor, cost);
                    prev.put(neighbor, p);
                }
                if (open.contains(neighbor) && dist.get(neighbor) > cost) {
                    dist.put(neighbor, cost);
                    open.update(neighbor, cost + ManhattanDistance.distance(neighbor, start));
                    prev.put(neighbor, p);
                }
            }
        }
        return false;
    }

    public int abstractDist(Pos pos) {
        if (closed.contains(pos))
            return dist.get(pos);
        if (resume(pos))
            return dist.get(pos);
        return Integer.MAX_VALUE;
    }

    public int synchronizedAbstractDist(Pos pos) {
        synchronized (dist) {
            return abstractDist(pos);
        }
    }

    public List<Pos> synchronizedGetPath(Pos pos) {
        synchronized (dist) {
            return getPath(pos);
        }
    }

    public List<Pos> getPath(Pos pos) {
        if (closed.contains(pos) || resume(pos)) {
            List<Pos> result = build(pos);
            result.add(end);
            Collections.reverse(result);
            result.remove(result.size() - 1);
            return result;
        }
        return null;
    }
}
