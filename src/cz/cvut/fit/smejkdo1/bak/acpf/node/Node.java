package cz.cvut.fit.smejkdo1.bak.acpf.node;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;

import java.util.Objects;

public class Node {
    private Pos pos;
    private NodeState type;
    private Team occupiedBy;

    public Node() {
        this.pos = new Pos();
        this.type = NodeState.EMPTY;
        this.occupiedBy = Team.NONE;
    }

    public Node(Pos pos, NodeState type) {
        this.pos = pos;
        this.type = type;
        this.occupiedBy = Team.NONE;
    }

    public Pos getPos() {
        return pos;
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }

    public NodeState getType() {
        return type;
    }

    public void setType(NodeState type) {
        this.type = type;
    }

    public Team getTeam() {
        return this.occupiedBy;
    }

    public void setTeam(Team team) {
        this.occupiedBy = team;
    }

    public void cleanUp() {
        this.occupiedBy = Team.NONE;
    }

    public Node deepCopy() {
        return new Node(pos, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(pos, node.pos) &&
                type == node.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, type);
    }
}
