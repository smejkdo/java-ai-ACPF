package cz.cvut.fit.smejkdo1.bak.acpf.machine.data;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;

public interface FMInput {
    int toInt();

    int getArg(int i);

    boolean getBit(int i);

    void init(GameMap gameMap, Agent agent);

}
