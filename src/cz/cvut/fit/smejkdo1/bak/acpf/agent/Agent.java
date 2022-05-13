package cz.cvut.fit.smejkdo1.bak.acpf.agent;

import cz.cvut.fit.smejkdo1.bak.acpf.astar.ReverseResumableAStar;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.*;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.NodeState;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;

import java.util.List;

public class Agent {
    private static int idCounter = 0;
    private Pos pos;
    private final int id;
    private int priority;
    private final Pos target;
    private int state = 0;
    private final FSM fsm;


    private final Team team;
    private final GameMap gameMap;
    private final ReverseResumableAStar aStar;

    private Pos previousPos;
    private Pos upcomingPos;
    private List<Pos> fastPath;
    private AgentCommunicator communicator;

    private Direction direction;
    private final Pos start;

    public Agent(Pos start, Team team, FSM fsm, GameMap gameMap, Pos target) {
        this.id = idCounter++;
        this.pos = start;
        this.start = start;
        previousPos = start;
        this.team = team;
        this.fsm = fsm;
        this.gameMap = gameMap;
        this.gameMap.updatePosition(start, this);
        this.aStar = new ReverseResumableAStar(start, target, gameMap);
        this.target = target;
        this.priority = getDistanceToTarget();
    }

    public Agent(Pos start, Team team, FSM fsm, GameMap gameMap, Pos target, int id) {
        this.id = id;
        this.pos = start;
        this.start = start;
        previousPos = start;
        this.team = team;
        this.fsm = fsm;
        this.gameMap = gameMap;
        this.gameMap.updatePosition(start, this);
        this.aStar = new ReverseResumableAStar(start, target, gameMap);
        this.target = target;
        this.priority = getDistanceToTarget();
    }

    public Pos takeTurn() {
        if (isOnTarget()) {
            if (isCommunicating()) {
                if (communicator == null)
                    communicator = new AgentCommunicator(this, gameMap);
                AgentCommunicator.getGlobalTermination(gameMap).markAsIdle(id);
            }
            upcomingPos = pos;

        } else {
            lookAround(); //check for inputs
            prepareMove();
        }
        return upcomingPos;
    }

    private void prepareMove() {
        switch (state) {
            case 1:
                goLeft();
                return;
            case 2:
                goRight();
                return;
            case 3:
                goBack();
        }
    }

    private void lookAround() {
        FMInput fmInput;
        useAStar();
        findDirection();
        switch (fsm.getInputStyle()) {
            case DATA_FROM_MAP:
                fmInput = new DataFromMap();
                break;
            case INFORMED_VICINITY:
                fmInput = new InformedVicinity();
                break;
            case EIGHT_VICINITY:
                fmInput = new EightVicinity();
                break;
            case COMPLEX_EIGHT_VICINITY:
                fmInput = new ComplexEightVicinity();
                break;
            case INFO_EXCHANGE:
                communicator = new AgentCommunicator(this, gameMap);
                fmInput = new InfoExchange();
                break;
            default:
                throw new UnsupportedOperationException();
        }

        fmInput.init(gameMap, this);
        state = fsm.makeShift(fmInput, state);

        if (fastPath != null && !fastPath.isEmpty())
            fastPath.remove(fastPath.size() - 1);
    }


    private void useAStar() {
        if (!isOnTarget()) {
            if (fastPath == null
                    || fastPath.size() == 0
                    || !fastPath.get(fastPath.size() - 1).isAdjacent(this.pos)) {
                fastPath = aStar.getPath(pos);
            }
            if (fastPath != null)
                if (gameMap.getNode(fastPath.get(fastPath.size() - 1)).getType() == NodeState.EMPTY) {
                    upcomingPos = fastPath.get(fastPath.size() - 1);
                    return;
                }
        }
        upcomingPos = pos;
    }

    public void resetMove() {
        upcomingPos = pos;
    }

    public void clearNode() {
        gameMap.clearNode(this.pos);
        this.previousPos = pos;
    }

    public void updatePosition() {
        gameMap.updatePosition(upcomingPos, this);
        pos = upcomingPos;
        upcomingPos = null;
    }

    public int getDistanceToTarget() {
        return aStar.abstractDist(pos);
        /*if (fastPath != null && !fastPath.isEmpty() && fastPath.get(fastPath.size() - 1).isAdjacent(pos)) {
            return fastPath.size();
        }
        fastPath = aStar.getPath(pos);
        if (fastPath == null)
            return ManhattanDistance.distance(pos, target);
        return fastPath.size();*/
    }

    public void findDirection() {
        Pos tmp = pos.sub(upcomingPos);
        if (tmp.y == -1)
            direction = Direction.RIGHT;
        else if (tmp.y == 1)
            direction = Direction.LEFT;
        else if (tmp.x == -1)
            direction = Direction.DOWN;
        else if (tmp.x == 1)
            direction = Direction.UP;
        else {
            tmp = pos.sub(target);
            if (tmp.y < 0)
                direction = Direction.RIGHT;
            else if (tmp.y > 0)
                direction = Direction.LEFT;
            else if (tmp.x < 0)
                direction = Direction.DOWN;
            else if (tmp.x > 0)
                direction = Direction.UP;
        }
    }

    private void newUpcomingPos(Direction direction1) {
        Pos newPos;
        if (direction1 == null) {
            upcomingPos = pos;
            return;
        }
        switch (direction1) {
            case UP:
                newPos = pos.add(new Pos(-1, 0));
                break;
            case DOWN:
                newPos = pos.add(new Pos(1, 0));
                break;
            case LEFT:
                newPos = pos.add(new Pos(0, -1));
                break;
            case RIGHT:
                newPos = pos.add(new Pos(0, 1));
                break;
            default:
                return;
        }
        if (gameMap.getNode(newPos).getType() != NodeState.WALL)
            upcomingPos = newPos;
    }

    private void goLeft() {
        if (direction == null)
            return;
        switch (direction) {
            case UP:
                newUpcomingPos(Direction.LEFT);
                break;
            case DOWN:
                newUpcomingPos(Direction.RIGHT);
                break;
            case LEFT:
                newUpcomingPos(Direction.DOWN);
                break;
            case RIGHT:
                newUpcomingPos(Direction.UP);
        }
        findDirection();
    }

    private void goRight() {
        if (direction == null)
            return;
        switch (direction) {
            case UP:
                newUpcomingPos(Direction.RIGHT);
                break;
            case DOWN:
                newUpcomingPos(Direction.LEFT);
                break;
            case LEFT:
                newUpcomingPos(Direction.UP);
                break;
            case RIGHT:
                newUpcomingPos(Direction.DOWN);
        }
        findDirection();
    }

    private void goBack() {
        if (direction == null)
            return;
        switch (direction) {
            case UP:
                newUpcomingPos(Direction.DOWN);
                break;
            case DOWN:
                newUpcomingPos(Direction.UP);
                break;
            case LEFT:
                newUpcomingPos(Direction.RIGHT);
                break;
            case RIGHT:
                newUpcomingPos(Direction.LEFT);
        }
        findDirection();
    }

    public boolean isOnTarget() {
        return pos.equals(target);
    }

    public Pos getPos() {
        return pos;
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }

    public Pos getTarget() {
        return target;
    }

    public Team getTeam() {
        return team;
    }

    public Pos getPreviousPos() {
        return previousPos;
    }

    public void setPreviousPos(Pos previousPos) {
        this.previousPos = previousPos;
    }

    public Pos getUpcomingPos() {
        return upcomingPos;
    }

    public void setUpcomingPos(Pos upcomingPos) {
        this.upcomingPos = upcomingPos;
    }

    public List<Pos> getFastPath() {
        return fastPath;
    }

    public void setFastPath(List<Pos> fastPath) {
        this.fastPath = fastPath;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public AgentCommunicator getCommunicator() {
        return communicator;
    }

    public void setCommunicator(AgentCommunicator communicator) {
        this.communicator = communicator;
    }

    public ReverseResumableAStar getaStar() {
        return aStar;
    }

    public void reinitialize() {
        this.pos = start;
        previousPos = start;
        this.gameMap.updatePosition(start, this);
        state = 0;
        upcomingPos = null;
        fastPath = null;
        direction = null;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getId() {
        return id;
    }


    public boolean isCommunicating() {
        return fsm.getInputStyle().equals(InputStyle.INFO_EXCHANGE);
    }
}
