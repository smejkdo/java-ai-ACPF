package cz.cvut.fit.smejkdo1.bak.acpf.node;

import java.util.Objects;

public class Pos3D extends Pos {
    public int time;

    public Pos3D(int x, int y, int time) {
        super(x, y);
        this.time = time;
    }

    public Pos3D(Pos pos, int time) {
        super(pos.x, pos.y);
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pos3D)) return false;
        if (!super.equals(o)) return false;
        Pos3D pos3D = (Pos3D) o;
        return time == pos3D.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), time);
    }

    @Override
    public String toString() {
        return "Pos3D{" +
                "x=" + x +
                ", y=" + y +
                ", time=" + time +
                '}';
    }

    public Pos3D decreasedTime(int i) {
        return new Pos3D(x, y, time - i);
    }


    public Pos3D increasedTime(int i) {
        return new Pos3D(x, y, time + i);
    }

    public Pos to2D() {
        return new Pos(x, y);
    }
}
