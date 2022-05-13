package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.inttree;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.FMInput;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IntTreeNodeData implements Serializable, Comparable<IntTreeNodeData> {
    public static final int ARG_NUMBER_MAX_BITS = 5;
    public static final int ARG_THRESHOLD_MAX_BITS = 5;
    private int argNumber;
    private int argThreshold;

    public IntTreeNodeData(int argNumber, int argThreshold) {
        this.argNumber = argNumber;
        this.argThreshold = argThreshold;
    }

    public IntTreeNodeData(IntTreeNodeData intTreeNodeData) {
        this.argNumber = intTreeNodeData.argNumber;
        this.argThreshold = intTreeNodeData.argThreshold;
    }

    /**
     * @param argThreshold used as transition
     */
    public IntTreeNodeData(int argThreshold) {
        this.argThreshold = argThreshold;
        argNumber = -1;
    }

    public static List<IntTreeNodeData> buildList(List<String> strList) {
        List<IntTreeNodeData> result = new ArrayList<>();
        for (String s :
                strList) {
            result.add(build(s));
        }
        return result;
    }

    public static IntTreeNodeData build(String str) {
        if (str.contains(":")) {
            String[] args = str.split(":");
            return new IntTreeNodeData(Integer.decode(args[0]), Integer.decode(args[1]));
        } else {
            return new IntTreeNodeData(Integer.decode(str));
        }
    }

    public void mutate(int mutationRate, InputStyleUtils info) {
        if (argNumber == -1)
            for (int i = 0; i < 2; i++) { //hardcoded number of states
                if (Rand.nextInt(100) < mutationRate)
                    mutateThreshold(i);
            }
        else {
            int bound = Integer.numberOfTrailingZeros(Integer.highestOneBit(info.getNumberOfArguments())) + 1;
            for (int i = 0; i < bound; i++) {
                if (Rand.nextInt(100) < mutationRate)
                    mutateNumber(i);
            }
            if (argNumber >= info.getNumberOfArguments())
                argNumber = Rand.nextInt(info.getNumberOfArguments());
            for (int i = 0; i < info.getMaxLengthOfArgumentInBits(argNumber); i++) {
                if (Rand.nextInt(100) < mutationRate)
                    mutateThreshold(i);
            }
        }
    }

    private void mutateNumber(int i) {
        argNumber = argNumber ^ (1 << (i));
    }

    private void mutateThreshold(int i) {
        argThreshold = argThreshold ^ (1 << (i));
    }

    public int getArgNumber() {
        return argNumber;
    }

    public void setArgNumber(int argNumber) {
        this.argNumber = argNumber;
    }

    public int getArgThreshold() {
        return argThreshold;
    }

    public void setArgThreshold(int argThreshold) {
        this.argThreshold = argThreshold;
    }

    public IntTreeNodeData deepCopy() {
        return new IntTreeNodeData(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntTreeNodeData)) return false;
        IntTreeNodeData intTreeNodeData = (IntTreeNodeData) o;
        return argNumber == intTreeNodeData.argNumber &&
                argThreshold == intTreeNodeData.argThreshold;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argNumber, argThreshold);
    }

    @Override
    public String toString() {
        if (argNumber != -1)
            return String.format("%d:%d", argNumber, argThreshold);
        else
            return String.format("%d", argThreshold);
    }


    @Override
    public int compareTo(IntTreeNodeData intTreeNodeData) {
        if (this.argNumber == -1 || intTreeNodeData.argNumber == -1)
            return 1;
        else return -1;
    }

    public boolean isLeaf() {
        return getArgNumber() == -1;
    }

    public <A> int difference(A oth) {
        if (oth instanceof IntTreeNodeData)
            return Math.abs(argNumber - ((IntTreeNodeData) oth).argNumber)
                    + Math.abs(argThreshold - ((IntTreeNodeData) oth).argThreshold);
        else
            return (argNumber + argThreshold) * 2;
    }

    public int getNewState() {
        if (argNumber != -1)
            throw new UnsupportedOperationException();
        else
            return argThreshold;
    }

    public boolean transition(FMInput arg) {
        return arg.getArg(argNumber) > argThreshold;
    }
}
