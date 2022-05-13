package cz.cvut.fit.smejkdo1.bak.acpf.map;

import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.target.TargetFinder;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.NumberPadding;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoadMap {
    public static final int numberOfMyMaps = 19;
    public static final int numberOfMovingaiMaps = 28;
    public static final int maxEvolutionMapSize = 50;
    public static final int[] evolutionMaps = new int[]{0, 2, 4, 5};
    public static final int[] testMaps = new int[]{};
    public static final int maxTestMapSize = 50;
    public static final boolean mirrors = false;

    public static PrepareMap fetchFile(String fileName) {
        List<String> result = FetchFile.lines(fileName);
        return new PrepareMap(result);
    }

    public static List<GameMap> fetchAllMyEvolutionMaps() {
        return fetchMyMaps(evolutionMaps);
    }

    public static List<GameMap> fetchAllMyMaps() {

        List<GameMap> result = new ArrayList<>();
        GameMap gameMap;
        for (int i = 0; i < numberOfMyMaps; i++) {
            try {
                gameMap = LoadMap.fetchFile("resources/Maps/" +
                        NumberPadding.intPadding(i, 3) + "/map.wad").parseInput();
                result.add(gameMap);
            } catch (NullPointerException e) {
                System.err.println("Map file number " + i + " not found.");
                e.printStackTrace();
            }
        }

        return result;
    }


    private static List<GameMap> fetchAllMyTestMaps() {
        return fetchMyMaps(testMaps);
    }

    private static List<GameMap> fetchMyMaps(int[] mapNumbers) {
        List<GameMap> result = new ArrayList<>();
        GameMap gameMap;
        for (int i : mapNumbers) {
            try {
                gameMap = LoadMap.fetchFile("resources/Maps/" +
                        NumberPadding.intPadding(i, 3) + "/map.wad").parseInput();
                result.add(gameMap);
            } catch (NullPointerException e) {
                System.err.println("Map file number " + i + " not found.");
                e.printStackTrace();
            }
        }

        return result;
    }

    private static List<Pair<GameMap, List<Pair<Pos, Pos>>>> mirrorMapAndTarget(GameMap gameMap,
                                                                                List<Pair<Pos, Pos>> targets) {
        List<Pair<GameMap, List<Pair<Pos, Pos>>>> result = new ArrayList<>();
        GameMap mirrored = gameMap.mirrorMapHorizontally();
        if (!mirrored.equals(gameMap))
            result.add(new Pair<>(mirrored,
                    TargetFinder.mirrorTargetHorizontally(targets, gameMap.getMapSize())));
        mirrored = gameMap.mirrorMapVertically();
        if (!mirrored.equals(gameMap))
            result.add(new Pair<>(mirrored,
                    TargetFinder.mirrorTargetVertically(targets, gameMap.getMapSize())));
        return result;
    }

    public static GameMap fetchMyMap(int mapNumber) {
        GameMap gameMap;
        try {
            gameMap = LoadMap.fetchFile("resources/Maps/" +
                    NumberPadding.intPadding(mapNumber, 3) + "/map.wad").parseInput();
        } catch (NullPointerException e) {
            System.err.println("Map file number " + mapNumber + " not found.");
            e.printStackTrace();
            return null;
        }
        return gameMap;
    }

    public static List<Pair<GameMap, List<Pair<Pos, Pos>>>> fetchAllEvolutionMapData() {
        List<Pair<GameMap, List<Pair<Pos, Pos>>>> result = fetchAllMyEvolutionMapData();
        result.addAll(fetchAllMovingaiMapData());
        result.addAll(fetchAllMovingaiMapData());
        result.addAll(fetchAllMovingaiMapData());
        result.addAll(fetchAllMovingaiMapData());
        result.addAll(fetchAllMovingaiMapData());
        result.addAll(fetchAllMovingaiMapData());
        return result.stream().filter(pair ->
                pair.getKey().getMapSize().x < LoadMap.maxEvolutionMapSize
                        && pair.getKey().getMapSize().y < LoadMap.maxEvolutionMapSize)
                .collect(Collectors.toList());
    }

    private static List<Pair<GameMap, List<Pair<Pos, Pos>>>> fetchAllMyEvolutionMapData() {
        List<GameMap> maps = LoadMap.fetchAllMyEvolutionMaps();
        List<List<Pair<Pos, Pos>>> targets = TargetFinder.fetchAllTargetsForAllMyEvolutionMaps(evolutionMaps);
        return getMapAndTargetWithMirror(maps, targets);
    }

    public static List<Pair<GameMap, List<Pair<Pos, Pos>>>> fetchAllMyMapData() {
        List<GameMap> maps = LoadMap.fetchAllMyMaps();
        List<List<Pair<Pos, Pos>>> targets = TargetFinder.fetchAllTargetsForAllMyMaps(numberOfMyMaps);
        return getMapAndTargetWithMirror(maps, targets);
    }


    private static List<Pair<GameMap, List<Pair<Pos, Pos>>>> fetchAllMyTestMapData() {
        List<GameMap> maps = LoadMap.fetchAllMyTestMaps();
        List<List<Pair<Pos, Pos>>> targets = TargetFinder.fetchAllTargetsForAllMyEvolutionMaps(testMaps);
        return getMapAndTargetWithMirror(maps, targets);
    }


    public static Pair<GameMap, List<Pair<Pos, Pos>>> fetchMyMapData(int mapNum) {
        return new Pair<>(fetchMyMap(mapNum), TargetFinder.fetchAllTargetsForMyMap(mapNum));
    }

    private static List<Pair<GameMap, List<Pair<Pos, Pos>>>> getMapAndTargetWithMirror(List<GameMap> maps,
                                                                                       List<List<Pair<Pos, Pos>>> targets) {
        List<Pair<GameMap, List<Pair<Pos, Pos>>>> mapAndTarget = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            mapAndTarget.add(new Pair<>(maps.get(i), targets.get(i)));
            if (mirrors)
                mapAndTarget.addAll(mirrorMapAndTarget(maps.get(i), targets.get(i)));
        }

        return mapAndTarget;
    }

    public static List<Pair<GameMap, List<Pair<Pos, Pos>>>> fetchAllMovingaiMapData() {
        List<GameMap> maps = LoadMap.fetchAllMovingaiMaps();
        List<List<Pair<Pos, Pos>>> targets = TargetFinder.fetchAllTargetsForAllMovingaiMaps();
        return getMapAndTargetWithMirror(maps, targets);
    }

    public static Pair<GameMap, List<Pair<Pos, Pos>>> fetchMovingaiMapData(String mapName, int scenario) {
        GameMap map = LoadMap.fetchMovingaiMap(mapName);
        List<Pair<Pos, Pos>> targets = TargetFinder.fetchTargetsForMovingaiMap(mapName, scenario);
        return new Pair<>(map, targets);
    }

    private static GameMap fetchMovingaiMap(String mapName) {
        return LoadMap
                .fetchFile("resources/Maps/Downloaded/movingai.com/"
                        + mapName + ".map").parseInput();
    }

    private static List<GameMap> fetchAllMovingaiMaps() {
        File movingaiDirectory = FetchFile.fetchDirectory("resources/Maps/Downloaded/movingai.com");
        if (movingaiDirectory == null)
            return null;
        File[] maps = movingaiDirectory.listFiles();
        List<GameMap> result = new ArrayList<>();
        if (maps == null)
            return null;
        for (File map : maps) {
            if (map.isDirectory())
                continue;
            result.add(new PrepareMap(FetchFile.lines(map)).parseInput());
        }

        return result;
    }

    public static List<Pair<GameMap, List<Pair<Pos, Pos>>>> fetchAllMapData() {
        List<Pair<GameMap, List<Pair<Pos, Pos>>>> result = fetchAllMyMapData();
        result.addAll(fetchAllMovingaiMapData());
        return result;
    }

    public static List<Pair<GameMap, List<Pair<Pos, Pos>>>> fetchAllTestMapsData() {
        List<Pair<GameMap, List<Pair<Pos, Pos>>>> result = fetchAllMyTestMapData();
        result.add(fetchMovingaiMapData("maze-32-32-2", Rand.nextInt(25) + 1));
        result.add(fetchMovingaiMapData("room-32-32-4", Rand.nextInt(25) + 1));
        result.add(fetchMovingaiMapData("random-32-32-20", Rand.nextInt(25) + 1));
        result.add(fetchMovingaiMapData("empty-8-8", Rand.nextInt(25) + 1));
        result.add(fetchMovingaiMapData("empty-16-16", Rand.nextInt(25) + 1));
        return result;
    }

}
