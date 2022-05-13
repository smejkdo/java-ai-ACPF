package cz.cvut.fit.smejkdo1.bak.acpf.machine.data;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;

public class EightVicinity implements FMInput {
    /**
     * [0] - wall
     * [1] - target
     * [2] - EnemyAgent
     * [3] - FriendlyAgent
     */
    //all 2bit => 16bit
    protected static final int MAX_BITS_PER_VISION = 2;
    protected boolean[][] vicinity = new boolean[][]{{false, false, false, false}, {false, false, false, false},
            {false, false, false, false}, {false, false, false, false},
            {false, false, false, false}, {false, false, false, false},
            {false, false, false, false}, {false, false, false, false}};

    public static InputStyleUtils inputStyleUtils = new InputStyleUtils(16,
            8, new int[]{2, 2, 2, 2, 2, 2, 2, 2}, InputStyle.EIGHT_VICINITY);


    public EightVicinity() {

    }

    public void init(GameMap gameMap, Agent agent) {
        searchVicinity(gameMap, agent);
    }

    public static String csvTags() {
        return "UP_VISION;UP_LEFT_VISION;LEFT_VISION;DOWN_LEFT_VISION;DOWN_VISION;DOWN_RIGHT_VISION;RIGHT_VISION;UP_RIGHT_VISION";
    }

    public static void heuristics(TransitionInterface transition) {
        transition.symmetry(new int[]{2, 3, 4, 5, 6, 7}, new int[]{10, 11, 12, 13, 14, 15},
                new int[]{1, 2, 3}, new int[]{5, 6, 7});
    }

    protected int startOffset(Agent agent) {
        switch (agent.getDirection()) {
            case LEFT:
                return 2;
            case DOWN:
                return 4;
            case RIGHT:
                return 6;
            default:
                return 0;
        }
    }

    protected void searchVicinity(GameMap gameMap, Agent agent) {
        int start = startOffset(agent);

        for (int i = 0; i < 8; i++) {
            int vision = VicinitySearch.searchDirection(start, agent.getPos(), gameMap);
            start = (start + 1) % 8;
            vicinity[i][vision] = true;
        }
    }

    @Override
    public int toInt() {
        int result = 0;
        int tmp;
        for (boolean[]field : vicinity) {
            if (field[0])
                tmp = 0;
            else if (field[1])
                tmp = 1;
            else if (field[2])
                tmp = 2;
            else
                tmp = 3;
            result = (result<<MAX_BITS_PER_VISION) + tmp;
        }
        return result;
    }


    @Override
    public int getArg(int i) {
        return ((vicinity[i][1] ? 1 : 0) + (vicinity[i][2] ? 2 : 0) + (vicinity[i][3] ? 3 : 0));
    }

    @Override
    public boolean getBit(int i) {
        int visIdx = i / MAX_BITS_PER_VISION;
        int bitIdx = i % MAX_BITS_PER_VISION;
        return vicinity[visIdx][bitIdx];
    }
}
