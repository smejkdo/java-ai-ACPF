package cz.cvut.fit.smejkdo1.bak.acpf.machine.data;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Node;
import cz.cvut.fit.smejkdo1.bak.acpf.node.NodeState;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;

public class VicinitySearch {

    private static final int up = 0;
    private static final int upLeft = 1;
    private static final int left = 2;
    private static final int downLeft = 3;
    private static final int down = 4;
    private static final int downRight = 5;
    private static final int right = 6;
    private static final int upRight = 7;

    public static int searchDirection(int start, Pos agentPos, GameMap gameMap) {
        Pos step = computeStep(start);
        return search(step, agentPos, gameMap);
    }

    public static int[] distanceComplex(int start, Pos agentPos, GameMap gameMap) {
        Pos step = computeStep(start);
        return searchComplex(step, agentPos, gameMap);
    }

    private static Pos computeStep(int start) {
        Pos step;
        switch (start) {
            case up:
                step = new Pos(-1, 0);
                break;
            case upLeft:
                step = new Pos(-1, -1);
                break;
            case left:
                step = new Pos(0, -1);
                break;
            case downLeft:
                step = new Pos(1, -1);
                break;
            case down:
                step = new Pos(1, 0);
                break;
            case downRight:
                step = new Pos(1, 1);
                break;
            case right:
                step = new Pos(0, 1);
                break;
            default:
                step = new Pos(-1, 1);
        }
        return step;
    }

    private static int search(Pos step, Pos agentPos, GameMap gameMap) {
        Pos pos = new Pos(agentPos.x, agentPos.y);
        do {
            pos = pos.add(step);
        } while (gameMap.getNode(pos).getType().equals(NodeState.EMPTY)
                && gameMap.getNode(pos).getTeam().equals(Team.NONE));

        Node n = gameMap.getNode(pos);

        if (!n.getTeam().equals(Team.NONE)) {
            if (n.getTeam() == gameMap.getNode(agentPos).getTeam())
                return 3;
            else
                return 2;
        }
        else if (n.getType().equals(NodeState.WALL))
            return 1;
        else
            return -1;
    }

    private static int[] searchComplex(Pos step, Pos agentPos, GameMap gameMap) {
        Pos pos = new Pos(agentPos.x, agentPos.y);
        int dist = 0;
        do {
            pos = pos.add(step);
            dist++;
        } while (gameMap.getNode(pos).getType().equals(NodeState.EMPTY)
                && gameMap.getNode(pos).getTeam().equals(Team.NONE));

        Node n = gameMap.getNode(pos);

        if (!n.getTeam().equals(Team.NONE)) {
            int priority = gameMap.agentPriorityOnPos(n.getPos());
            if (n.getTeam() == gameMap.getNode(agentPos).getTeam())
                return new int[]{3, dist, priority};
            else
                return new int[]{2, dist, -1};
        } else if (n.getType().equals(NodeState.WALL))
            return new int[]{1, dist, -1};
        else
            return new int[]{-1};
    }

}
