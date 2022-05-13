package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.*;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;

import java.util.List;

public class TransitionBuilder {
    public static TransitionInterface build(int mutationProbability, InputStyle inputStyle, OutputStyle outputStyle) {
        TransitionInterface result;
        switch (outputStyle) {
            case TRANSITION_LIST:
                result = new TransitionList(4);
                break;
            case INT_TREE:
                result = new DecisionTrees(4);
                break;
            case BOOL_TREE:
                result = new BoolTrees(4);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        InputStyleUtils isu;
        switch (inputStyle) {
            case INFORMED_VICINITY:
                isu = InformedVicinity.inputStyleUtils;
                break;
            case EIGHT_VICINITY:
                isu = EightVicinity.inputStyleUtils;
                break;
            case DATA_FROM_MAP:
                isu = DataFromMap.inputStyleUtils;
                break;
            case COMPLEX_EIGHT_VICINITY:
                isu = ComplexEightVicinity.inputStyleUtils;
                break;
            case INFO_EXCHANGE:
                isu = InfoExchange.inputStyleUtils;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        result.fillRand(mutationProbability, isu);

        return result;
    }

    public static TransitionInterface build(List<String> list) {
        if (list.isEmpty()) {
            throw new UnsupportedOperationException("Loaded file is empty.");
        }
        TransitionInterface t;
        switch (OutputStyle.valueOf(list.get(0))) {
            case TRANSITION_LIST:
                return TransitionList.build(list);
            case INT_TREE:
                return DecisionTrees.build(list);
            case BOOL_TREE:
                return BoolTrees.build(list);
            default:
                throw new UnsupportedOperationException("Corrupted file loaded.");
        }

    }
}
