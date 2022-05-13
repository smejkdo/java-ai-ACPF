package cz.cvut.fit.smejkdo1.bak.acpf.map;

import cz.cvut.fit.smejkdo1.bak.acpf.astar.AStar;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;

import java.util.*;

public class MapMaker {
    private List<String> map = new ArrayList<>();
    private Pair<String, String> positions;
    private Set<Pos> taboo = new HashSet<>();
    private int y; //rows
    private int x; //columns
    private int count; //num of agents


    public MapMaker(int y, int x, int count) {
        this.y = y;
        this.x = x;
        this.count = count;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String s;
            int i;
            System.out.println("Write probability of generating a wall: ");
            while (scanner.hasNext()) {
                s = scanner.nextLine();
                i = Integer.decode(s);
                new MapMaker(50, 75, 20).newMake(i);
                System.out.println("Write probability of generating a wall: ");
            }
        }
    }

    public void make() {
        Set<Integer> xWalls = new HashSet<>();
        Set<Integer> yWalls = new HashSet<>();

        int xWallsNumber = Rand.nextInt(x);
        int yWallsNumber = Rand.nextInt(y);

        for (int i = 0; i < xWallsNumber; i++)
            xWalls.add(Rand.nextInt(x));
        for (int i = 0; i < yWallsNumber; i++)
            yWalls.add(Rand.nextInt(y));


        map.add("#".repeat(x));
        for (int i = 0; i < y - 2; i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < x; j++) {
                if (j == 0 || j == x - 1)
                    str.append("#");
                else {
                    if (xWalls.contains(j))
                        str.append(wallOrDoor());
                    else if (yWalls.contains(i))
                        str.append(wallOrDoor());
                    else
                        str.append(" ");

                }
            }
            map.add(str.toString());
        }
        map.add("#".repeat(x));
        makePositions();
        printMap();
        saveMap();
    }

    public void newMake(int probabilityOfWalls) {

        map.add("#".repeat(x));
        for (int i = 0; i < y - 2; i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < x; j++) {
                if (j == 0 || j == x - 1)
                    str.append("#");
                else {
                    if (Rand.nextInt(100) < probabilityOfWalls)
                        str.append("#");
                    else
                        str.append(" ");

                }
            }
            map.add(str.toString());
        }
        map.add("#".repeat(x));
        makePositions();
        printMap();
        saveMap();
    }

    private void printMap() {
        for (int j = 0; j < map.size(); j++) {
            String line = map.get(j);
            for (int i = 0; i < line.length(); i++) {
                if (taboo.contains(new Pos(j, i)))
                    System.out.print('.');
                else
                    System.out.print(line.charAt(i));
            }
            System.out.print("\n");
        }
    }

    private void saveMap() {
        Scanner sc = new Scanner(System.in);
        String in;
        System.out.println("Write exit to pass this map or folder name to create and save map into:");
        if (sc.hasNext()) {
            in = sc.nextLine();
            if (in.equals("exit") || in.equals("e"))
                return;
            else {
                FetchFile.save(positions.getKey(),
                        "resources/Maps/" + in, "/red_agents_data.wad");
                FetchFile.save(positions.getValue(),
                        "resources/Maps/" + in, "/blu_agents_data.wad");
                FetchFile.save(map, "resources/Maps/" + in + "/map.wad");
            }
        }
    }

    public void makePositions(){
        GameMap gameMap = new PrepareMap(map).parseInput();
        List<Pair<Pos, Pos>> targets = new ArrayList<>();
        while (targets.size() < count){
            Pos startingPos = findPosition();
            Pos targetPos = findPosition();
            List<Pos> nodes = new AStar().findPath(startingPos, targetPos, gameMap);
            if (nodes == null){
                taboo.remove(startingPos);
                taboo.remove(targetPos);
                continue;
            }


            targets.add(new Pair<>(startingPos, targetPos));
        }
        StringBuilder[] sb = new StringBuilder[] {new StringBuilder(), new StringBuilder()};
        int i = 0;
        for (Pair<Pos, Pos> pair :
                targets) {
            sb[i].append(pair.getKey().x).append("\n")
                    .append(pair.getKey().y).append("\n")
                    .append(pair.getValue().x).append("\n")
                    .append(pair.getValue().y).append("\n/\n");
            i = 1 - i;
        }
        positions = new Pair<>(sb[0].toString(), sb[1].toString());

    }

    public String wallOrDoor() {
        if (Rand.nextDouble() < 0.3)
            return " ";
        return "#";
    }

    private Pos findPosition() {
        Pos pos;
        do {
            pos = new Pos(Rand.nextInt(y), Rand.nextInt(x));
        }
        while (taboo.contains(pos)
                || map.get(pos.x).charAt(pos.y) == '#');
        taboo.add(pos);
        return pos;
    }
}
