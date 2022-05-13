package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.FMInput;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransitionList implements Serializable, TransitionInterface {
    private List<List<Integer>> transitions;
    private InputStyleUtils inputStyleUtils;

    public TransitionList(int states) {
        transitions = new ArrayList<>();
        for (int i = 0; i < states; i++) {
            transitions.add(new ArrayList<>());
        }
    }

    public TransitionList(int states, InputStyleUtils inputStyleUtils) {
        this(states);
        this.inputStyleUtils = inputStyleUtils;
    }

    public TransitionList() {
    }

    public static TransitionList build(List<String> list) {
        TransitionList t = new TransitionList(Integer.decode(list.get(2)));
        t.inputStyleUtils = InputStyleUtils.getInstance(InputStyle.valueOf(list.get(1)));
        t.setAndDecodeTransition(list);
        return t;
    }

    @Override
    public void fillRand(int mutationProbability, InputStyleUtils inputStyleUtils) {
        int max = 1 << (inputStyleUtils.getNumberOfBits());
        this.inputStyleUtils = inputStyleUtils;

        int j = 1 << 6;
        j -= 1;
        j = (j << 7);

        for (int i = 0; i < max; i++) {
            if (inputStyleUtils.getInputStyle()
                    .equals(InputStyle.INFORMED_VICINITY)
                    && (j & i) == 0) {
                transitions.get(0).add(0);
                transitions.get(1).add(0);
                transitions.get(2).add(0);
                transitions.get(3).add(0);
                continue;
            }
            if (Rand.nextInt(100) < mutationProbability)
                transitions.get(0).add(Rand.nextInt(3) + 1);
            else
                transitions.get(0).add(0);
            if (Rand.nextInt(100) < mutationProbability)
                transitions.get(1).add(Rand.nextInt(3) + 1);
            else
                transitions.get(1).add(0);
            if (Rand.nextInt(100) < mutationProbability)
                transitions.get(2).add(Rand.nextInt(3) + 1);
            else
                transitions.get(2).add(0);
            if (Rand.nextInt(100) < mutationProbability)
                transitions.get(3).add(Rand.nextInt(3) + 1);
            else
                transitions.get(3).add(0);
        }
    }

    @Override
    public int getNumberOfBits() {
        int result = transitions.stream().mapToInt(List::size).sum();
        assert transitions.size() == 4;
        result *= Integer.numberOfTrailingZeros(Integer.highestOneBit(transitions.size()));
        return result;
    }

    @Override
    public TransitionInterface deepCopy() {
        TransitionList result = new TransitionList();
        result.transitions = new ArrayList<>();
        for (int i = 0; i < transitions.size(); i++) {
            result.transitions.add(new ArrayList<>());
            for (int j = 0; j < transitions.get(i).size(); j++) {
                result.transitions.get(i).add(transitions.get(i).get(j));
            }
        }
        result.inputStyleUtils = this.inputStyleUtils;
        return result;
    }

    @Override
    public List<TransitionInterface> crossover(TransitionInterface genotype) {
        if (!genotype.getClass().equals(this.getClass()))
            throw new UnsupportedOperationException("Crossover of different classes is not supported.");
        if (Rand.nextBoolean())
            return crossHalf((TransitionList) genotype);
        else
            return crossBitwise((TransitionList) genotype);
    }

    @Override
    public void repairSelf() {
        //Unused for now.
    }

    @Override
    public int difference(TransitionInterface other) {
        int result = 0;
        if (!other.getClass().equals(this.getClass()))
            return this.getNumberOfBits();
        for (int i = 0; i < transitions.size(); i++) {
            for (int j = 0; j < transitions.get(i).size(); j++) {
                if (!transitions.get(i).get(j).equals(((TransitionList) other).transitions.get(i).get(j)))
                    result++;
            }
        }
        return result;
    }

    /**
     * If bits on indexes bits1 and bits2 are swapped, it is expected that values should be equal, or rather symmetrical.
     *
     * @param bits1 first indexes of symmetrical bits
     * @param bits2 second indexes of symmetrical bits
     * @param args1 first numbers of symmetrical arguments
     * @param args2 second numbers of symmetrical arguments
     */
    @Override
    public void symmetry(int[] bits1, int[] bits2, int[] args1, int[] args2) {
        int mask1 = 0;
        int mask2 = 0;
        for (int i = 0; i < bits1.length; i++) {
            mask1 += 1 << bits1[i];
            mask2 += 1 << bits2[i];
        }
        int bitDifference = Integer.numberOfTrailingZeros(mask2) - Integer.numberOfTrailingZeros(mask1);
        if (bitDifference < 0) {
            int tmp = mask1;
            mask1 = mask2;
            mask2 = tmp;
            bitDifference = -bitDifference;
        }

        int bound = transitions.get(0).size();
        for (int i = 0; i < bound; i++) { //could be n/2 cycles
            int j = (i & ((1 << inputStyleUtils.getNumberOfBits() + 1) - 1 - mask2 - mask1)) + ((i & (mask1)) << bitDifference) + ((i & (mask2)) >> bitDifference);
            symmetricSolutions(i, j);
        }

    }

    /**
     * Changes solution with input value of i to solution with input
     * value of j or vice versa depending on random boolean.
     *
     * @param i first input value
     * @param j second input value
     */
    private void symmetricSolutions(int i, int j) {
        for (List<Integer> list :
                transitions) {
            if (list.get(j).equals(list.get(j)))
                continue;
            if (Rand.nextBoolean()) {
                symmetrySwap(j, i, list);
            } else {
                symmetrySwap(i, j, list);
            }
        }
    }

    /**
     * Sets value on index j in list to value on index i symmetrically.
     *
     * @param i    first input value
     * @param j    second input value
     * @param list list to change
     */
    private void symmetrySwap(int i, int j, List<Integer> list) {
        switch (list.get(i)) { //hardcoded states
            case 0:
                list.set(j, 0);
                break;
            case 1:
                list.set(j, 2);
                break;
            case 2:
                list.set(j, 1);
                break;
            case 3:
                list.set(j, 3);
                break;
            default:
                throw new IllegalArgumentException("Argument" + list.get(i)
                        + " on index " + i + " is unexpected as state.");
        }
    }

    private List<TransitionInterface> crossBitwise(TransitionList other) {
        List<TransitionInterface> result = new ArrayList<>();
        result.add(new TransitionList(this.transitions.size(), inputStyleUtils));
        result.add(new TransitionList(this.transitions.size(), inputStyleUtils));

        int i = 0;

        for (int k = 0; k < transitions.size(); k++) {
            for (int l = 0; l < transitions.get(k).size(); l++) {
                if (Rand.nextDouble() > .9)
                    i = 1 - i;
                ((TransitionList) result.get(i)).transitions.get(k).add(this.transitions.get(k).get(l));
                ((TransitionList) result.get(1 - i)).transitions.get(k).add(other.transitions.get(k).get(l));
            }
        }

        return result;
    }

    private List<TransitionInterface> crossHalf(TransitionList other) {
        int i = Rand.nextInt(transitions.size());
        int j = Rand.nextInt(transitions.get(i).size());
        List<TransitionInterface> result = new ArrayList<>();
        result.add(new TransitionList(this.transitions.size(), inputStyleUtils));
        result.add(new TransitionList(this.transitions.size(), inputStyleUtils));

        //0 -> i-1
        for (int k = 0; k < i; k++) {
            for (int l = 0; l < transitions.get(k).size(); l++) {
                ((TransitionList) result.get(0)).transitions.get(k).add(this.transitions.get(k).get(l));
                ((TransitionList) result.get(1)).transitions.get(k).add(other.transitions.get(k).get(l));
            }
        }

        //i
        for (int l = 0; l < j; l++) {
            ((TransitionList) result.get(0)).transitions.get(i).add(this.transitions.get(i).get(l));
            ((TransitionList) result.get(1)).transitions.get(i).add(other.transitions.get(i).get(l));
        }

        //Half

        for (int l = j; l < transitions.get(i).size(); l++) {
            ((TransitionList) result.get(1)).transitions.get(i).add(this.transitions.get(i).get(l));
            ((TransitionList) result.get(0)).transitions.get(i).add(other.transitions.get(i).get(l));
        }

        //i+1 -> size()
        for (int k = i + 1; k < transitions.size(); k++) {
            for (int l = 0; l < transitions.get(k).size(); l++) {
                ((TransitionList) result.get(1)).transitions.get(k).add(this.transitions.get(k).get(l));
                ((TransitionList) result.get(0)).transitions.get(k).add(other.transitions.get(k).get(l));

            }
        }
        assert ((TransitionList) result.get(1)).transitions.get(i).size() == this.transitions.get(i).size();
        assert ((TransitionList) result.get(0)).transitions.get(0).size() == other.transitions.get(0).size();
        return result;
    }


    @Override
    public int transition(FMInput arg, int state) {
        return this.transitions.get(state).get(arg.toInt());
    }

    @Override
    public OutputStyle getOutputStyle() {
        return OutputStyle.TRANSITION_LIST;
    }

    @Override
    public InputStyle getInputStyle() {
        return inputStyleUtils.getInputStyle();
    }

    @Override
    public void mutateBit(int i) {
        int idx = i / Integer.numberOfTrailingZeros(Integer.highestOneBit(transitions.size()));
        int bitIdx = i % Integer.numberOfTrailingZeros(Integer.highestOneBit(transitions.size()));
        int rowIdx = idx / transitions.get(0).size(); //all are same length
        int colIdx = idx % transitions.get(0).size();

        int num = transitions.get(rowIdx).get(colIdx);
        num = num ^ (1 << bitIdx);
        transitions.get(rowIdx).set(colIdx, num);
    }

    @Override
    public void mutate(int mutationRate) {
        int bound = getNumberOfBits();
        for (int i = 0; i < bound; i++) {
            if (Rand.nextInt(100) < mutationRate)
                mutateBit(i);
        }
    }

    @Override
    public void save(String fileName, String filePath) {
        FetchFile.save(toString(), filePath, fileName);
    }

    /**
     * Creates compact representation of this transition.
     *
     * @return string containing data possible to decode
     */
    public String transitionRepresentation() {
        StringBuilder result = new StringBuilder();
        result.append((transitions.size()))
                .append("\n")
                .append(transitions.get(0).size());
        long compactNum = 0;
        int shiftCnt = 0;
        for (List<Integer> transition :
                transitions) {
            String num;
            for (Integer integer : transition) {
                for (int j = 0; j < 2; j++) {
                    compactNum = (compactNum << 1) | ((integer >> j) & 1);

                    shiftCnt++;
                    if (shiftCnt >= Long.SIZE) {
                        result.append("\n");
                        result.append(compactNum);
                        shiftCnt = 0;
                        compactNum = 0;
                    }
                }
            }
        }
        if (shiftCnt != 0) {
            result.append("\n");
            result.append(compactNum);
        }
        return result.toString();
    }

    /**
     * Decodes saved transition representation.
     *
     * @param representation transition representation
     * @return lists for each state of transitions
     */
    public List<List<Integer>> decodeTransition(List<String> representation) {
        int states = Integer.decode(representation.get(2));
        int numberOfBits = Integer.numberOfTrailingZeros(Integer.highestOneBit(states));
        int innerIdx = 0;
        int numbersInCompact = Long.SIZE / numberOfBits;
        int listSize = Integer.decode(representation.get(3));
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < states; i++) {
            result.add(new ArrayList<>());
        }

        for (int i = 4; i < representation.size(); i++) {
            long compactNumber = Long.decode(representation.get(i));
            if (result.get(innerIdx).size() + numbersInCompact < listSize)
                result.get(innerIdx).addAll(decodeInt(compactNumber, numberOfBits));
            else {
                List<Integer> decoded = decodeInt(compactNumber, numberOfBits);
                for (int num : decoded) {
                    if (!(result.get(innerIdx).size() < listSize)) {
                        innerIdx++;
                        if (innerIdx >= states)
                            break;
                    }
                    result.get(innerIdx).add(num);
                }
            }

        }


        return result;
    }

    public void setAndDecodeTransition(List<String> representation) {
        transitions = decodeTransition(representation);
    }

    public List<List<Integer>> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<List<Integer>> transitions) {
        this.transitions = transitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransitionList)) return false;
        TransitionList that = (TransitionList) o;
        return Objects.equals(transitions, that.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transitions);
    }

    /**
     * decodes values from single long from saved file
     *
     * @param compactNumber number to decode
     * @param numberOfBits  number of bits of number
     * @return list of decoded numbers
     */
    private List<Integer> decodeInt(long compactNumber, int numberOfBits) {
        List<Integer> result = new ArrayList<>();
        int decodedNumber = 0;
        int bitIdx = 0;
        compactNumber = Long.reverse(compactNumber);
        for (int i = 0; i <= Long.SIZE; i++) {
            if (bitIdx >= numberOfBits) {
                bitIdx = 0;
                result.add(decodedNumber);
                decodedNumber = 0;
                if (i == Long.SIZE)
                    break;
            }
            decodedNumber = (decodedNumber) | ((int) (compactNumber & 1) << (bitIdx));
            compactNumber = (compactNumber >> 1);
            bitIdx++;
        }
        return result;
    }

    @Override
    public String toString() {
        return OutputStyle.TRANSITION_LIST + "\n" +
                inputStyleUtils.toString() + "\n" +
                transitionRepresentation();
    }
}