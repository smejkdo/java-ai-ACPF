package cz.cvut.fit.smejkdo1.bak.acpf.machine.data;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.AgentCommunicator;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;

public class InfoExchange implements FMInput {

    public static InputStyleUtils inputStyleUtils = new InputStyleUtils(1,
            1, new int[]{1}, InputStyle.INFO_EXCHANGE);

    public InfoExchange() {
    }

    public static void heuristics(TransitionInterface transition) {
        //empty
    }

    @Override
    public int toInt() {
        return 0;
    }

    @Override
    public int getArg(int i) {
        return 0;
    }

    @Override
    public boolean getBit(int i) {
        return false;
    }

    @Override
    public void init(GameMap gameMap, Agent agent) {
        AgentCommunicator.getGlobalTermination(gameMap).lockBeforeRun();
        AgentCommunicator.getGlobalTermination(gameMap).markAsRunning(agent.getId());
        agent.getCommunicator().checkPlan();
        AgentCommunicator.getGlobalTermination(gameMap).lockThread();

        agent.setFastPath(agent.getCommunicator().getResult());
        if (agent.getFastPath() != null && !agent.getFastPath().isEmpty())
            agent.setUpcomingPos(
                    agent.getFastPath()
                            .get(agent.getFastPath().size() - 1));
        else
            agent.setUpcomingPos(agent.getPos());
        agent.findDirection();
    }

    public static String csvTags() {
        return "NONE";
    }
}
