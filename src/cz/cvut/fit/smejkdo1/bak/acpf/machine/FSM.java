package cz.cvut.fit.smejkdo1.bak.acpf.machine;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.data.FMInput;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;

import java.util.Objects;

public class FSM {
    private TransitionInterface transition;
    private InputStyle inputStyle;
    private OutputStyle outputStyle;

    public FSM(InputStyle inputStyle, OutputStyle outputStyle) {
        this.setInputStyle(inputStyle);
        this.setOutputStyle(outputStyle);
    }

    public FSM(InputStyle inputStyle, OutputStyle outputStyle, TransitionInterface transition) {
        this(inputStyle, outputStyle);
        this.transition = transition;
    }

    public FSM(TransitionInterface transition) {
        this.transition = transition;
        this.inputStyle = transition.getInputStyle();
        this.outputStyle = transition.getOutputStyle();
    }

    public FSM(String transitionName) {
        setupTransitions(transitionName);
        this.inputStyle = transition.getInputStyle();
        this.outputStyle = transition.getOutputStyle();
    }

    public int makeShift(FMInput input, int state){

        return transition.transition(input, state);
    }

    public void setupTransitions(){
        setupTransitions("");
    }

    public String transitionSuffix(int i){
        return '-' + Integer.toString(10000 + i).substring(1);
    }

    public void setupTransitions(String s){
        setTransition(TransitionBuilder.build(FetchFile.lines("resources/FiniteAutomatons/" + s + ".wad")));
    }


    //getters and setters

    public TransitionInterface getTransition() {
        return transition;
    }

    public void setTransition(TransitionInterface transition) {
        this.transition = transition;
    }

    public InputStyle getInputStyle() {
        return inputStyle;
    }

    public void setInputStyle(InputStyle inputStyle) {
        this.inputStyle = inputStyle;
    }

    public OutputStyle getOutputStyle() {
        return outputStyle;
    }

    public void setOutputStyle(OutputStyle outputStyle) {
        this.outputStyle = outputStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FSM)) return false;
        FSM fsm = (FSM) o;
        return transition.equals(fsm.transition) &&
                inputStyle == fsm.inputStyle &&
                outputStyle == fsm.outputStyle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transition, inputStyle, outputStyle);
    }
}

