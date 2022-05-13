package cz.cvut.fit.smejkdo1.bak.acpf.target;

import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.NumberPadding;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TargetFinder {
    private static final int maxAgentsOnOneMap = 40;

    /**
     * Fetches positions for all agents on map, represented by pairs of starting and target positions.
     * 'null' pair used to separate teams.
     *
     * @param mapNum specifies which map to load positions for.
     * @return array of pairs of positions.
     */
    public static List<Pair<Pos, Pos>> fetchAllTargetsForMyMap(int mapNum) {
        List<String> lines = FetchFile.lines(
                "resources/Maps/"
                        + NumberPadding.intPadding(mapNum, 3)
                        + "/red_agents_data.wad");
        lines.add(null);
        lines.addAll(
                Objects.requireNonNull(
                        FetchFile.lines(
                                "resources/Maps/"
                                        + NumberPadding.intPadding(mapNum, 3)
                                        + "/blu_agents_data.wad")));
        return parseMyInput(lines);
    }

    public static List<List<Pair<Pos, Pos>>> fetchAllTargetsForAllMyMaps(int numberOfMaps) {
        List<List<Pair<Pos, Pos>>> result = new ArrayList<>();
        for (int i = 0; i < numberOfMaps; i++) {
            result.add(fetchAllTargetsForMyMap(i));
        }
        return result;
    }

    /**
     * parses lines into array of pairs of positions
     *
     * @param lines lines from file where positions are saved
     * @return array of pairs of positions
     */
    private static List<Pair<Pos, Pos>> parseMyInput(List<String> lines) {
        List<Pair<Pos, Pos>> result = new ArrayList<>();
        Pos tmpStart;
        Pos tmpTarget;
        if (lines.size() > 0 && lines.get(0).replaceAll("\\s+", " ").split("\\s").length > 1)
            return parseMyLineInput(lines);
        for (int i = 0; i < lines.size(); i += 5) {
            if (lines.get(i) == null) { //splits red and blu agents positions
                result.add(null);
                i -= 4;
                continue;
            }
            tmpStart = new Pos(Integer.decode(lines.get(i)), Integer.decode(lines.get(i + 1)));
            tmpTarget = new Pos(Integer.decode(lines.get(i + 2)), Integer.decode(lines.get(i + 3)));
            result.add(new Pair<>(tmpStart, tmpTarget));
        }
        return result;
    }

    private static List<Pair<Pos, Pos>> parseMyLineInput(List<String> lines) {
        List<Pair<Pos, Pos>> result = new ArrayList<>();
        for (String line : lines) {
            if (line == null) { //splits red and blu agents positions
                result.add(null);
                continue;
            }
            String[] tmp = line.replaceAll("\\s+", " ").split("\\s");
            if (tmp.length < 4)
                continue;
            result.add(new Pair<>(new Pos(Integer.decode(tmp[0]), Integer.decode(tmp[1])),
                    new Pos(Integer.decode(tmp[2]), Integer.decode(tmp[3]))));
        }
        return result;
    }

    public static List<List<Pair<Pos, Pos>>> fetchAllTargetsForAllMyEvolutionMaps(int[] evolutionMaps) {
        List<List<Pair<Pos, Pos>>> result = new ArrayList<>();
        for (int i : evolutionMaps) {
            result.add(fetchAllTargetsForMyMap(i));
        }
        return result;
    }

    public static List<Pair<Pos, Pos>> mirrorTargetHorizontally(List<Pair<Pos, Pos>> targets, Pos mapSize) {
        List<Pair<Pos, Pos>> result = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i) == null) {
                result.add(null);
                continue;
            }
            Pos start = new Pos(mapSize.x - 1 - targets.get(i).getKey().x, targets.get(i).getKey().y);
            Pos target = new Pos(mapSize.x - 1 - targets.get(i).getValue().x, targets.get(i).getValue().y);
            result.add(new Pair<>(start, target));
        }
        return result;
    }

    public static List<Pair<Pos, Pos>> mirrorTargetVertically(List<Pair<Pos, Pos>> targets, Pos mapSize) {
        List<Pair<Pos, Pos>> result = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i) == null) {
                result.add(null);
                continue;
            }
            Pos start = new Pos(targets.get(i).getKey().x, mapSize.y - 1 - targets.get(i).getKey().y);
            Pos target = new Pos(targets.get(i).getValue().x, mapSize.y - 1 - targets.get(i).getValue().y);
            result.add(new Pair<>(start, target));
        }
        return result;
    }

    public static List<List<Pair<Pos, Pos>>> fetchAllTargetsForAllMovingaiMaps() {
        List<String> mapNames = fetchMovingaiMapNames();
        //hardcoded movingai agent data location
        String directoryName = "resources/Maps/Downloaded/movingai.com/scen-even/";
        String fileSuffix = "-even-" + (Rand.nextInt(25) + 1) + ".scen";
        List<List<Pair<Pos, Pos>>> result = new ArrayList<>();
        File f;
        for (int i = 0; i < mapNames.size(); i++) {
            f = new File(directoryName + mapNames.get(i) + fileSuffix);
            result.add(fetchPositionsForMovingaiMap(f));
        }
        return result;
    }

    private static List<Pair<Pos, Pos>> fetchPositionsForMovingaiMap(File f) {
        List<Pair<Pos, Pos>> result = new ArrayList<>();
        List<String> lines = FetchFile.lines(f);
        lines = lines.subList(1, lines.size());
        int split = Math.min(maxAgentsOnOneMap / 2, lines.size() / 2);
        Collections.shuffle(lines);
        for (int i = 0; i < lines.size() && i < maxAgentsOnOneMap; i++) {
            if (i == split)
                result.add(null);
            String line = lines.get(i);
            result.add(parseMovingaiInput(line));
        }
        return result;
    }

    private static Pair<Pos, Pos> parseMovingaiInput(String line) {
        String[] data = line.replaceAll("\\s+", " ").split("\\s");
        return new Pair(
                new Pos(Integer.decode(data[5]) + 1,
                        Integer.decode(data[4]) + 1),
                new Pos(Integer.decode(data[7]) + 1,
                        Integer.decode(data[6]) + 1));
    }

    private static List<String> fetchMovingaiMapNames() {
        //hardcoded movingai maps location
        File movingaiDirectory = FetchFile.fetchDirectory("resources/Maps/Downloaded/movingai.com");

        if (movingaiDirectory == null)
            return null;
        String[] maps = movingaiDirectory.list();
        if (maps == null)
            return null;
        List<String> result = new ArrayList<>();
        for (String map :
                maps) {
            if (map.endsWith(".map"))
                result.add(map.substring(0, map.length() - 4));
        }
        return result;
    }

    public static List<Pair<Pos, Pos>> fetchTargetsForMovingaiMap(String mapName, int scenario) {
        String fileSuffix = "-even-" + scenario + ".scen";
        File file = new File("resources/Maps/Downloaded/movingai.com/scen-even/"
                + mapName + fileSuffix);
        return fetchPositionsForMovingaiMap(file);
    }
}




/*
    public void initTargets(int num, GameMap gameMap) {
        targets = new ArrayList<>();
        while (targets.size() < num) {
            Pos newTargetPos = new Pos(Rand.nextInt(gameMap.getMapSize().x), Rand.nextInt(gameMap.getMapSize().y));
            if (gameMap.getNode(newTargetPos).getType().equals(NodeState.EMPTY)) {
                targets.add(newTargetPos);
                gameMap.getNode(newTargetPos).setType(NodeState.TARGET);
            }
        }
    }

    public void agentTargetsSet(List<Agent> redAgents, List<Agent> bluAgents) {

        redAgents.forEach(this::giveAgentTarget);
        bluAgents.forEach(this::giveAgentTarget);
    }

    private void giveAgentTarget(Agent a) {
        a.setTarget(targets.get(index));
        ++index;
    }

    public void shuffleTargets() {
        Collections.shuffle(targets);
    }


    /**
     *
     *
     * @param agent which agent to assign the target to
     * @return pos of target
     */
    /*public Pos find(Agent agent) {
        /*if(Main.gameMap.getTargets().size())
            initTargets(1, Main.gameMap);*/
    /*    int i = Rand.nextInt(gameMap.getTargets().size());
        Pos pos = gameMap.getTargets().get(i);
        //Main.gameMap.getTargets().remove(i);
        return pos;
    }

    public List<Pos> getTargets() {
        return targets;
    }

    public void setTargets(List<Pos> targets) {
        this.targets = targets;
    }*/
