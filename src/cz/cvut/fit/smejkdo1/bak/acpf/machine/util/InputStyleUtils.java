package cz.cvut.fit.smejkdo1.bak.acpf.machine.util;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.*;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;

public class InputStyleUtils {
    private int numberOfBits;
    private int numberOfArguments;
    private int[] maxLengthOfArgumentsInBits;
    private InputStyle inputStyle;


    public InputStyleUtils(int numberOfBits,
                           int numberOfArguments,
                           int[] maxLengthOfArgumentsInBits,
                           InputStyle inputStyle) {
        this.numberOfBits = numberOfBits;
        this.numberOfArguments = numberOfArguments;
        this.maxLengthOfArgumentsInBits = maxLengthOfArgumentsInBits;
        this.inputStyle = inputStyle;
    }

    public int getNumberOfBits() {
        return numberOfBits;
    }

    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    public int[] getMaxLengthOfArgumentsInBits() {
        return maxLengthOfArgumentsInBits;
    }

    public int getMaxLengthOfArgumentInBits(int index) {
        return maxLengthOfArgumentsInBits[index];
    }

    public InputStyle getInputStyle() {
        return inputStyle;
    }

    public static InputStyleUtils getInstance(InputStyle inputStyle) {
        switch (inputStyle) {
            case INFORMED_VICINITY:
                return InformedVicinity.inputStyleUtils;
            case DATA_FROM_MAP:
                return DataFromMap.inputStyleUtils;
            case EIGHT_VICINITY:
                return EightVicinity.inputStyleUtils;
            case COMPLEX_EIGHT_VICINITY:
                return ComplexEightVicinity.inputStyleUtils;
            case INFO_EXCHANGE:
                return InfoExchange.inputStyleUtils;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static void heuristic(InputStyle inputStyle, TransitionInterface transition) {
        switch (inputStyle) {
            case INFORMED_VICINITY:
                InformedVicinity.heuristics(transition);
                break;
            case DATA_FROM_MAP:
                DataFromMap.heuristics(transition);
                break;
            case EIGHT_VICINITY:
                EightVicinity.heuristics(transition);
                break;
            case COMPLEX_EIGHT_VICINITY:
                ComplexEightVicinity.heuristics(transition);
                break;
            case INFO_EXCHANGE:
                InfoExchange.heuristics(transition);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String csvTags(InputStyle inputStyle) {
        switch (inputStyle) {
            case INFORMED_VICINITY:
                return InformedVicinity.csvTags();
            case DATA_FROM_MAP:
                return DataFromMap.csvTags();
            case EIGHT_VICINITY:
                return EightVicinity.csvTags();
            case COMPLEX_EIGHT_VICINITY:
                return ComplexEightVicinity.csvTags();
            case INFO_EXCHANGE:
                return InfoExchange.csvTags();
            default:
                throw new UnsupportedOperationException();
        }
    }


    @Override
    public String toString() {
        return inputStyle.toString();
    }
}
