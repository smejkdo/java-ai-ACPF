package cz.cvut.fit.smejkdo1.bak.acpf.map;

import cz.cvut.fit.smejkdo1.bak.acpf.astar.ManhattanDistance;
import cz.cvut.fit.smejkdo1.bak.acpf.node.NodeState;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.util.*;

public class PairMapNodes {
    private GameMap gameMap;
    private List<Pair<Pos, Pos>> pairs = new ArrayList<>();

    public PairMapNodes(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public PairMapNodes(String filename) {
        this.gameMap = LoadMap.fetchFile(filename).parseInput();
    }

    public static void main(String[] args) {
        PairMapNodes pmn = new PairMapNodes("resources/Maps/018/map.wad");
        pmn.init();
        System.out.print(pmn.getTargets());
    }

    public void init() {
        LinkedList<Pos> list = new LinkedList<>();
        for (int i = 1; i < gameMap.getMapSize().x - 1; i++) {
            for (int j = 1; j < gameMap.getMapSize().y - 1; j++) {
                Pos pos = new Pos(i, j);
                if (gameMap.getNode(pos).getType().equals(NodeState.EMPTY))
                    list.add(pos);
            }
        }
        Collections.shuffle(list);

        while (list.size() > 1) {
            Pos start = list.pollFirst();
            Pos end = list.pollLast();
            pairs.add(new Pair<>(start, end));
        }
        pairs.sort(Comparator.comparingInt(pair -> ManhattanDistance
                .distance(pair.getKey(), pair.getValue())));
        Collections.reverse(pairs);


        Map<Integer, Integer> map = new HashMap<>();
        for (Pair<Pos, Pos> pair : pairs) {
            int dist = ManhattanDistance.distance(pair.getKey(), pair.getValue());
            map.put(dist, map.containsKey(dist) ? map.get(dist) + 1 : 1);
        }
        for (int i : map.keySet()) {
            System.out.println(i + ": " + map.get(i));
        }


    }

    public List<Pair<Pos, Pos>> getPairs() {
        return pairs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Pair<Pos, Pos> pair : pairs) {
            sb.append(pair.getKey().x).append(" ").append(pair.getKey().y).append(" ")
                    .append(pair.getValue().x).append(" ").append(pair.getValue().y).append("\n");
        }
        return sb.toString();
    }

    public String getTargets() {
        StringBuilder[] red = new StringBuilder[2];
        red[0] = new StringBuilder();
        red[1] = new StringBuilder();
        int i = 0;
        for (int i1 = 0; i1 < pairs.size() && i1 < 40; i1++) {
            Pair<Pos, Pos> pair = pairs.get(i1);
            red[i].append(pair.getKey().x).append(" ").append(pair.getKey().y).append(" ")
                    .append(pair.getValue().x).append(" ").append(pair.getValue().y).append("\n");
            i = 1 - i;
        }
        return red[0].toString() + "\n" + red[1].toString();
    }
}
