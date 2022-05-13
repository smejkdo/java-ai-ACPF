package cz.cvut.fit.smejkdo1.bak.acpf.map;

import cz.cvut.fit.smejkdo1.bak.acpf.node.Node;
import cz.cvut.fit.smejkdo1.bak.acpf.node.NodeState;
import cz.cvut.fit.smejkdo1.bak.acpf.node.NodeStateConverter;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrepareMap {
    private List<String> input;

    public PrepareMap(List<String> input) {
        this.input = input;
    }

    public GameMap parseInput() {
        if (input.get(0).contains("type"))
            return parseGlobalFormat();
        Pos mapSize = new Pos(input.size(), input.get(0).length());
        Map<Pos, Node> map = new HashMap<>();
        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.get(i).length(); j++) {
                Pos newPos = new Pos(i, j);
                map.put(newPos, new Node(newPos, NodeStateConverter.toNodeState(input.get(i).charAt(j))));
            }
        }
        return new GameMap(map, mapSize);
    }

    private GameMap parseGlobalFormat() {
        Pos mapSize = new Pos(Integer.decode(input.get(1).split(" ")[1]) + 2, Integer.decode(input.get(2).split(" ")[1]) + 2);
        Map<Pos, Node> map = new HashMap<>();
        Pos newPos;

        for (int j = 0; j < mapSize.y; j++) {
            newPos = new Pos(0, j);
            map.put(newPos, new Node(newPos, NodeState.WALL));
        }
        for (int i = 4; i < input.size(); i++) {
            newPos = new Pos(i - 3, 0);
            map.put(newPos, new Node(newPos, NodeState.WALL));
            for (int j = 0; j < mapSize.y - 2; j++) {
                newPos = new Pos(i - 3, j + 1);
                map.put(newPos, new Node(newPos, NodeStateConverter.toNodeState(input.get(i).charAt(j))));
            }
            newPos = new Pos(i - 3, mapSize.y - 1);
            map.put(newPos, new Node(newPos, NodeState.WALL));
        }
        for (int j = 0; j < mapSize.y; j++) {
            newPos = new Pos(mapSize.x - 1, j);
            map.put(newPos, new Node(newPos, NodeState.WALL));
        }


        return new GameMap(map, mapSize);
    }

}
