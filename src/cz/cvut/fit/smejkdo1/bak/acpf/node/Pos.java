package cz.cvut.fit.smejkdo1.bak.acpf.node;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputTranslator;

import java.util.List;
import java.util.Objects;


public class Pos {
    private static final int MAX_MAP_SIZE = 2;
    public int x;
    public int y;

    public Pos(){
        x = -1;
        y = -1;
    }

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pos add(Pos p2){
        return new Pos(this.x + p2.x, this.y + p2.y);
    }
    public Pos sub(Pos p2){
        return new Pos(this.x - p2.x, this.y - p2.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pos pos = (Pos) o;
        return x == pos.x &&
                y == pos.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public boolean outOfBounds(Pos p2){
        return (p2.x < 0 || p2.y < 0
                || p2.x >= this.x || p2.y >= this.y);
    }

    public List<Boolean> toBinary() {
        List<Boolean> result = toBinary(x);
        result.addAll(toBinary(y));
        return result;
    }
    private List<Boolean> toBinary(int j){
        return InputTranslator.intToBool(j, MAX_MAP_SIZE);
    }

    public boolean isAdjacent(Pos p2){
        return (Math.abs(this.x - p2.x) == 1 && Math.abs(this.y - p2.y) == 0)
                || (Math.abs(this.x - p2.x) == 0 && Math.abs(this.y - p2.y) == 1);
    }

    @Override
    public String toString() {
        return "Pos{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Pos3D as3D(int time) {
        return new Pos3D(x, y, time);
    }
}
