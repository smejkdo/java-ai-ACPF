package cz.cvut.fit.smejkdo1.bak.acpf.astar;

import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;

public class ManhattanDistance {
    public static int distance(Pos p1, Pos p2){
        return (Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y));
    }
}
