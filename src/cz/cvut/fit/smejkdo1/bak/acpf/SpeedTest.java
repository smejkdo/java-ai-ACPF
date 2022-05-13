package cz.cvut.fit.smejkdo1.bak.acpf;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.map.LoadMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.util.List;

public class SpeedTest {
    public static void main(String[] args) {
        TransitionInterface t1 = TransitionBuilder.build(
                FetchFile
                        .lines("resources/FiniteAutomatons/ASTAR.wad"));
        TransitionInterface t2 = TransitionBuilder.build(
                FetchFile
                        .lines("resources/FiniteAutomatons/ASTAR.wad"));

        FSM f1 = new FSM(t1);
        FSM f2 = new FSM(t2);

        int mapNumber = 18;
        Pair<GameMap, List<Pair<Pos, Pos>>> mapAndTargets = LoadMap.fetchMovingaiMapData("random-32-32-20", mapNumber);
        double start, end;
        while (true) {
            start = System.currentTimeMillis();
            Acpf.runMap(f1, f2, mapAndTargets);
            end = System.currentTimeMillis();
            System.out.println("Whole map: " + (end - start));
        }
    }
}
