package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.booltree;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.FMInput;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoolTreeNodeData implements Comparable<BoolTreeNodeData> {
    public int inputLength = 16;
    public int numOfStates = 4;
    private int argNumber;

    public BoolTreeNodeData(int argNumber) {
        this.argNumber = argNumber;
    }

    public BoolTreeNodeData(int argNumber, int inputLength, int numOfStates) {
        this.inputLength = inputLength;
        this.numOfStates = numOfStates;
        this.argNumber = argNumber;
    }


    public static List<BoolTreeNodeData> buildList(List<String> strList, InputStyleUtils inputStyleUtils) {
        List<BoolTreeNodeData> result = new ArrayList<>();
        int i = 0;
        int bound = 1;
        int depth = 1; //prohlednout prubeh
        for (String s : strList) {
            BoolTreeNodeData tmp = build(s);
            tmp.inputLength = inputStyleUtils.getNumberOfBits() - depth + 1;
            result.add(tmp);
            i++;
            if (i == bound) {
                i = 0;
                depth++;
                bound *= 2;
            }
        }
        return result;
    }

    public static BoolTreeNodeData build(String str) {
        return new BoolTreeNodeData(Integer.decode(str));
    }

    public BoolTreeNodeData deepCopy() {
        return new BoolTreeNodeData(argNumber, inputLength, numOfStates);
    }

    public boolean isLeaf() {
        return argNumber >= inputLength;
    }

    public void mutate(int mutationRate, InputStyleUtils info) {
        if (argNumber < inputLength) {
            int bound = Integer.numberOfTrailingZeros(Integer.highestOneBit(inputLength)) + 1;
            for (int i = 0; i < bound; i++) {
                if (Rand.nextInt(100) < mutationRate)
                    mutateNumber(i);
            }
            if (argNumber >= inputLength)
                argNumber = Rand.nextInt(inputLength);
        } else {
            for (int i = 0; i < 2; i++) { //hardcoded number of states
                if (Rand.nextInt(100) < mutationRate) {
                    argNumber -= inputLength;
                    mutateNumber(i);
                    argNumber += inputLength;
                }
            }
        }

    }

    private void mutateNumber(int i) {
        argNumber = argNumber ^ (1 << i);
    }

    public <A> int difference(A oth) {
        if (oth instanceof BoolTreeNodeData)
            return Math.abs(argNumber - ((BoolTreeNodeData) oth).argNumber);
        else
            return argNumber * 2;
    }

    @Override
    public String toString() {
        return String.valueOf(argNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoolTreeNodeData)) return false;
        BoolTreeNodeData that = (BoolTreeNodeData) o;
        return argNumber == that.argNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argNumber);
    }


    @Override
    public int compareTo(BoolTreeNodeData oth) {
        return this.argNumber - oth.argNumber;
    }

    public int getNewState() {
        if (argNumber - inputLength > 3)
            throw new IllegalStateException("");
        return argNumber - inputLength;
    }

    public boolean transition(FMInput arg) {
        return arg.getBit(argNumber);
    }

    public void changeInputLength(int inputLength) {
        if (this.inputLength == inputLength)
            return;
        if (argNumber >= this.inputLength) {
            argNumber -= this.inputLength;
            argNumber += inputLength;
        } else {
            if (inputLength <= 0) {
                argNumber = Rand.nextInt(numOfStates);
            } else {
                double tmp = (double) (argNumber + 1) / this.inputLength;
                tmp = inputLength * tmp;
                tmp -= 1;
                argNumber = (int) Math.round(tmp);
                argNumber = Math.min(Math.max(argNumber, 0), inputLength - 1);
            }
        }

        if (argNumber < 0) {
            System.err.println(argNumber);
        }
        if (argNumber - inputLength > 3)
            System.err.println("err");
        this.inputLength = inputLength;
    }

    public int getArgNumber() {
        return argNumber;
    }

    public void generateLeaf() {
        argNumber = Rand.nextInt(numOfStates) + inputLength;
    }


}
