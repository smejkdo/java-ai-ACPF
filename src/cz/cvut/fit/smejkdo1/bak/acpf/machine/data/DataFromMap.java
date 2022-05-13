package cz.cvut.fit.smejkdo1.bak.acpf.machine.data;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.astar.ManhattanDistance;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputTranslator;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Node;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataFromMap implements FMInput {//11 bit
    private int distToMyTarget; //3 - 8
    private static final int DIST_TO_MY_TARGET_MAX_BITS = 3;
    private int closestEnemy; //3 - 8
    private static final int CLOSEST_ENEMY_MAX_BITS = 3;
    private int closestTeammate; //3 - 8
    private static final int CLOSEST_TEAMMATE_MAX_BITS = 3;
    private boolean teammateOnMyPath; //1 - 2
    private boolean enemyOnMyPath; //1 - 2

    public static InputStyleUtils inputStyleUtils = new InputStyleUtils(11,
            5, new int[]{3, 3, 3, 1, 1}, InputStyle.DATA_FROM_MAP);

    public DataFromMap() {
    }

    public void init(GameMap gameMap, Agent agent) {
        distToMyTarget = agent.getDistanceToTarget();
        getClosest(gameMap, agent);
        getAgentsOnPath(agent, gameMap);
        //myProximity = gameMap.getProximity(agent, myProximitySize);
        makeApproximate();
    }

    public static String csvTags() {
        return "DIST_TO_MY_TARGET;CLOSEST_ENEMY;CLOSEST_TEAMMATE;TEAMMATE_ON_PATH;ENEMY_ON_PATH";
    }

    public static void heuristics(TransitionInterface transition) {

    }

    private void makeApproximate() {
        approxDistToMyTarget();
        approxClosest();
    }

    private void approxClosest() {
        closestEnemy = Integer.numberOfTrailingZeros(Integer.highestOneBit(closestEnemy));
        if (closestEnemy >= 1 << CLOSEST_ENEMY_MAX_BITS)
            closestEnemy = (1 << CLOSEST_ENEMY_MAX_BITS) - 1;
        closestTeammate = Integer.numberOfTrailingZeros(Integer.highestOneBit(closestTeammate));
        if (closestTeammate >= 1<<CLOSEST_TEAMMATE_MAX_BITS)
            closestTeammate = (1<<CLOSEST_TEAMMATE_MAX_BITS) - 1;
    }

    private void approxDistToMyTarget() {
        distToMyTarget = Integer.numberOfTrailingZeros(Integer.highestOneBit(distToMyTarget));
        if (distToMyTarget >= 1<<DIST_TO_MY_TARGET_MAX_BITS)
            distToMyTarget = (1<<DIST_TO_MY_TARGET_MAX_BITS) - 1;
    }

    public List<Boolean> toBinary(){
        List<Boolean> list = new ArrayList<>();
        list.addAll(InputTranslator.intToBool(distToMyTarget, DIST_TO_MY_TARGET_MAX_BITS));
        list.addAll(InputTranslator.intToBool(closestEnemy, CLOSEST_ENEMY_MAX_BITS));
        list.addAll(InputTranslator.intToBool(closestTeammate, CLOSEST_TEAMMATE_MAX_BITS));
        list.add(teammateOnMyPath);
        list.add(enemyOnMyPath);
        /*for (List<Integer> lst :
                myProximity) {
            for (Integer i : lst) {
                list.addAll(InputTranslator.intToBool(i, myProximityMaxBits));
            }
        }*/
        //System.out.println("Delka binarky je : " + list.size());
        return list;
    }

    @Override
    public int toInt() {
        int result = distToMyTarget;
        result = (result<<CLOSEST_ENEMY_MAX_BITS) + closestEnemy;
        result = (result<<CLOSEST_TEAMMATE_MAX_BITS) + closestTeammate;
        result = (result<<1) + (teammateOnMyPath?1:0);
        result = (result<<1) + (enemyOnMyPath?1:0);
        return result;
    }

    @Override
    public int getArg(int i) {
        switch (i) {
            case 0:
                return distToMyTarget;
            case 1:
                return closestEnemy;
            case 2:
                return closestTeammate;
            case 3:
                return (teammateOnMyPath ? 1 : 0);
            case 4:
                return (enemyOnMyPath ? 1 : 0);
        }
        return -1;
    }

    @Override
    public boolean getBit(int i) {
        if (i < DIST_TO_MY_TARGET_MAX_BITS)
            return InputTranslator.intToBool(distToMyTarget, DIST_TO_MY_TARGET_MAX_BITS).get(i);
        i -= DIST_TO_MY_TARGET_MAX_BITS;
        if (i < CLOSEST_ENEMY_MAX_BITS)
            return InputTranslator.intToBool(closestEnemy, CLOSEST_ENEMY_MAX_BITS).get(i);
        i -= CLOSEST_ENEMY_MAX_BITS;
        if (i < CLOSEST_TEAMMATE_MAX_BITS)
            return InputTranslator.intToBool(closestTeammate, CLOSEST_TEAMMATE_MAX_BITS).get(i);
        i -= CLOSEST_TEAMMATE_MAX_BITS;
        if (i < 1)
            return teammateOnMyPath;
        i -= 1;
        if (i < 1)
            return teammateOnMyPath;
        throw new UnsupportedOperationException();
    }

    private void getAgentsOnPath(Agent agent, GameMap gameMap) {
        enemyOnMyPath = false;
        teammateOnMyPath = false;
        if (agent.getFastPath() == null)
            return;
        for (Pos pos :
                agent.getFastPath()) {
            Node n = gameMap.getNode(pos);
            if (enemyOnMyPath && teammateOnMyPath)
                break;
            if (n.getTeam().equals(agent.getTeam()))
                teammateOnMyPath = true;
            else if (!n.getTeam().equals(Team.NONE))
                enemyOnMyPath = true;
        }
    }

    private void getClosest(GameMap gameMap, Agent agent) {
        Set<Pos> set = gameMap.getAgentPositionsSet();
        int dist;
        int minAllied = 9999; //ally
        int minEnemy = 9999; //enemy
        for (Pos pos : set) {
            if (pos.equals(agent.getPos()))
                continue;
            dist = ManhattanDistance.distance(pos, agent.getPos());
            if (gameMap.getNode(pos).getTeam().equals(agent.getTeam())
                    && dist < minAllied)
                minAllied = dist;
            else if (gameMap.getNode(pos).getTeam().equals(Team.NONE))
                System.err.println("chyba v getClosest, spatna pozice!!");
            else if (dist < minEnemy)
                minEnemy = dist;
        }
        closestEnemy = minEnemy;
        closestTeammate = minAllied;
    }


    


}