package cz.cvut.fit.smejkdo1.bak.acpf.agent;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AgentBuilder {
    public static Pair<List<Agent>, List<Agent>> buildAll(List<Pair<Pos, Pos>> targets, FSM redFSM, FSM bluFSM, GameMap gameMap) {
        List<Agent> redAgents = new ArrayList<>();
        List<Agent> bluAgents = new ArrayList<>();
        int j = targets.size();
        for (int i = 0; i < j; i++) {
            if(targets.get(i) == null){
                j = i + 1;
                break;
            }
            redAgents.add(new Agent(
                    targets.get(i).getKey(),
                    Team.RED, redFSM,
                    gameMap,
                    targets.get(i).getValue(),
                    i));
        }
        redAgents.sort(Comparator.comparingInt(Agent::getPriority));
        for (int i = 0; i < redAgents.size(); i++) {
            redAgents.get(i).setPriority(i);
        }

        for (int i = j; i < targets.size(); i++) {
            bluAgents.add(new Agent(
                    targets.get(i).getKey(),
                    Team.BLU, bluFSM,
                    gameMap,
                    targets.get(i).getValue(),
                    i - j));
        }
        bluAgents.sort(Comparator.comparingInt(Agent::getPriority));
        for (int i = 0; i < bluAgents.size(); i++) {
            bluAgents.get(i).setPriority(i);
        }

        return new Pair<>(redAgents, bluAgents);
    }
}
