package cz.cvut.fit.smejkdo1.bak.acpf.machine.data;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;

public class ComplexEightVicinity extends EightVicinity {
    public static InputStyleUtils inputStyleUtils = new InputStyleUtils(40,
            24, new int[]{2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1}, InputStyle.COMPLEX_EIGHT_VICINITY);
    private static int MAX_BITS_PER_DISTANCE = 2;
    private static int MAX_BITS_PER_PRIORITY = 1;
    private int[] distances = new int[8];
    private boolean[] priorities = new boolean[8];

    public ComplexEightVicinity() {
        super();

    }

    public void init(GameMap gameMap, Agent agent) {
        searchVicinity(gameMap, agent);
    }

    public static String csvTags() {
        return EightVicinity.csvTags() +
                ";UP_VISION;UP_LEFT_VISION;LEFT_VISION;DOWN_LEFT_VISION;" +
                "DOWN_VISION;DOWN_RIGHT_VISION;RIGHT_VISION;UP_RIGHT_VISION;" +
                "UP_PRIORITY;UP_LEFT_PRIORITY;LEFT_PRIORITY;DOWN_LEFT_PRIORITY;" +
                "DOWN_PRIORITY;DOWN_RIGHT_PRIORITY;RIGHT_PRIORITY;UP_RIGHT_PRIORITY";
    }

    public static void heuristics(TransitionInterface transition) {
        transition.symmetry(new int[]{2, 3, 4, 5, 6, 7, 18, 19, 20, 21, 22, 23}, new int[]{10, 11, 12, 13, 14, 15, 26, 27, 28, 29, 30, 31},
                new int[]{1, 2, 3, 9, 10, 11}, new int[]{5, 6, 7, 13, 14, 15});
    }

    protected void searchVicinity(GameMap gameMap, Agent agent) {
        int start = startOffset(agent);

        for (int i = 0; i < 8; i++) {
            int[] vision = VicinitySearch.distanceComplex(start, agent.getPos(), gameMap);
            start = (start + 1) % 8;
            vicinity[i][vision[0]] = true;
            distances[i] = vision[1];
            priorities[i] = vision[2] > agent.getPriority();
        }
        approximateDistances();
    }

    private void approximateDistances() {
        for (int i = 0; i < 8; i++) {
            distances[i] = Integer.numberOfTrailingZeros(Integer.highestOneBit(distances[i]));
            if (distances[i] >= (1 << MAX_BITS_PER_DISTANCE))
                distances[i] = (1 << MAX_BITS_PER_DISTANCE) - 1;
        }
    }

    public int toInt() {
        int result = super.toInt();
        for (int i = 0; i < 8; i++) {
            result = (result << MAX_BITS_PER_DISTANCE) + distances[i];
        }
        for (int i = 0; i < 8; i++) {
            result = (result << MAX_BITS_PER_PRIORITY) + (priorities[i] ? 1 : 0);
        }
        return result;
    }

    public int getArg(int i) {
        if (i < vicinity.length)
            return super.getArg(i);
        i -= vicinity.length;
        if (i < distances.length)
            return distances[i];
        i -= distances.length;
        if (i < priorities.length)
            return (priorities[i] ? 1 : 0);
        else
            throw new UnsupportedOperationException();
    }

    public boolean getBit(int i) {
        if (i < vicinity.length * MAX_BITS_PER_VISION)
            return super.getBit(i);
        i -= vicinity.length * MAX_BITS_PER_VISION;
        if (i < distances.length * MAX_BITS_PER_DISTANCE)
            return (distances[i / MAX_BITS_PER_DISTANCE] & (i % MAX_BITS_PER_DISTANCE)) != 0;
        i -= distances.length * MAX_BITS_PER_DISTANCE;
        if (i < priorities.length * MAX_BITS_PER_PRIORITY)
            return priorities[i];
        else
            throw new UnsupportedOperationException();
    }
}
