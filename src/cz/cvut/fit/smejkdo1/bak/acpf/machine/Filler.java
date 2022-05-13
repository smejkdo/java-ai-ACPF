package cz.cvut.fit.smejkdo1.bak.acpf.machine;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;

public class Filler {
    public static void main(String[] args) {
        int states = 4;
        FSM fsm = new FSM(InputStyle.COMPLEX_EIGHT_VICINITY, OutputStyle.TRANSITION_LIST);
        fsm.setTransition(TransitionBuilder.build(5,
                fsm.getInputStyle(), fsm.getOutputStyle()));

        System.out.println(fsm.getTransition().getNumberOfBits());

        System.out.println("Ukladam");
    }
}
