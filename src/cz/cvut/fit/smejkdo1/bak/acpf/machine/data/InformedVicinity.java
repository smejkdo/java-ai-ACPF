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

import java.util.Set;

public class InformedVicinity implements FMInput {

    private static final int DISTANCE_THRESHOLD = 3;
    private static final int MAX_BITS_PER_VISION = 2;
    private static final int DIST_TO_MY_TARGET_MAX_BITS = 3;
    private static final int CLOSEST_ENEMY_MAX_BITS = 3;
    public static InputStyleUtils inputStyleUtils = new InputStyleUtils(13,
            6, new int[]{2, 2, 2, 3, 3, 1}, InputStyle.INFORMED_VICINITY);
    private boolean[][] vicinity = new boolean[][]{
            {false, false, false, false}, {false, false, false, false},
            {false, false, false, false}};
    private GameMap gameMap;
    private int distToMyTarget; //3 - 8
    private int closestEnemy; //3 - 8
    private boolean enemyOnMyPath; //1 - 2
    //13bit

    public InformedVicinity() {
    }

    public void init(GameMap gameMap, Agent agent) {
        searchVicinity(gameMap, agent);
        processDistToMyTarget(agent);
        getClosest(gameMap, agent);
        getAgentsOnPath(agent);
    }

    public static String csvTags() {
        return "UP_RIGHT_VISION;UP_VISION;UP_LEFT_VISION;DISTANCE_TO_MY_TARGET;CLOSEST_ENEMY;ENEMY_ON_PATH";
    }

    public static void heuristics(TransitionInterface transition) {
        transition.symmetry(new int[]{0, 1}, new int[]{4, 5},
                new int[]{0}, new int[]{2});

    }

    private void processDistToMyTarget(Agent agent) {
        distToMyTarget = agent.getDistanceToTarget();
        distToMyTarget = Integer.numberOfTrailingZeros(Integer.highestOneBit(distToMyTarget));
        if (distToMyTarget >= 1 << DIST_TO_MY_TARGET_MAX_BITS)
            distToMyTarget = (1 << DIST_TO_MY_TARGET_MAX_BITS) - 1;
    }


    private void getAgentsOnPath(Agent agent) {
        enemyOnMyPath = false;
        if (agent.getFastPath() == null)
            return;
        for (Pos pos :
                agent.getFastPath()) {
            Node n = gameMap.getNode(pos);
            if (!(n.getTeam().equals(Team.NONE) || n.getTeam().equals(agent.getTeam()))) {
                enemyOnMyPath = true;
                break;
            }
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
            if ((!gameMap.getNode(pos).getTeam().equals(Team.NONE)
                    && !gameMap.getNode(pos).getTeam().equals(agent.getTeam()))
                    && dist < minEnemy) {
                minEnemy = dist;
            }
        }
        closestEnemy = minEnemy;
        closestEnemy = Integer.numberOfTrailingZeros(Integer.highestOneBit(closestEnemy));
        if (closestEnemy >= 1 << CLOSEST_ENEMY_MAX_BITS)
            closestEnemy = (1 << CLOSEST_ENEMY_MAX_BITS) - 1;
    }

    private void searchVicinity(GameMap gameMap, Agent agent) {
        int start;
        this.gameMap = gameMap;

        switch (agent.getDirection()) {
            case LEFT:
                start = 2;
                break;
            case DOWN:
                start = 4;
                break;
            case RIGHT:
                start = 6;
                break;
            default:
                start = 0;
        }
        int[] vision = VicinitySearch.distanceComplex(start - 1, agent.getPos(), gameMap);
        if (vision[1] < DISTANCE_THRESHOLD)
            vicinity[0][vision[0]] = true;
        else
            vicinity[0][0] = true;
        vision = VicinitySearch.distanceComplex(start, agent.getPos(), gameMap);
        if (vision[1] < DISTANCE_THRESHOLD)
            vicinity[1][vision[0]] = true;
        else
            vicinity[1][0] = true;
        vision = VicinitySearch.distanceComplex(start + 1, agent.getPos(), gameMap);
        if (vision[1] < DISTANCE_THRESHOLD)
            vicinity[2][vision[0]] = true;
        else
            vicinity[2][0] = true;
    }


    @Override
    public int toInt() {
        int result = 0;
        int tmp;
        for (boolean[] field : vicinity) {
            if (field[0])
                tmp = 0;
            else if (field[1])
                tmp = 1;
            else if (field[2])
                tmp = 2;
            else
                tmp = 3;
            result = (result << MAX_BITS_PER_VISION) + tmp;
        }
        result = (result << DIST_TO_MY_TARGET_MAX_BITS) + distToMyTarget;
        result = (result << CLOSEST_ENEMY_MAX_BITS) + closestEnemy;
        result = (result << 1) + (enemyOnMyPath ? 1 : 0);
        return result;
    }

    @Override
    public int getArg(int i) {
        if (i < 3)
            return ((vicinity[i][1] ? 1 : 0) + (vicinity[i][2] ? 2 : 0) + (vicinity[i][3] ? 3 : 0));
        else
            switch (i) {
                case 3:
                    return distToMyTarget;
                case 4:
                    return closestEnemy;
                case 5:
                    return (enemyOnMyPath ? 1 : 0);
            }
        return -1;
    }

    @Override
    public boolean getBit(int i) {
        if (i < MAX_BITS_PER_VISION * vicinity.length) {
            int visIdx = i / MAX_BITS_PER_VISION;
            int bitIdx = i % MAX_BITS_PER_VISION;
            return vicinity[visIdx][bitIdx];
        }
        i -= MAX_BITS_PER_VISION * vicinity.length;
        if (i < DIST_TO_MY_TARGET_MAX_BITS)
            return InputTranslator.intToBool(distToMyTarget, DIST_TO_MY_TARGET_MAX_BITS).get(i);
        i -= DIST_TO_MY_TARGET_MAX_BITS;
        if (i < CLOSEST_ENEMY_MAX_BITS)
            return InputTranslator.intToBool(closestEnemy, CLOSEST_ENEMY_MAX_BITS).get(i);
        i -= CLOSEST_ENEMY_MAX_BITS;
        if (i < 1)
            return enemyOnMyPath;
        throw new UnsupportedOperationException();
    }
}
