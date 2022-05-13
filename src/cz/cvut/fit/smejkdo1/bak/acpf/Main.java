package cz.cvut.fit.smejkdo1.bak.acpf;

import cz.cvut.fit.smejkdo1.bak.acpf.game.GameInstance;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;

public class Main {
    public static FSM redFM, bluFM;

    public static void println(Object o){
        System.out.println(o);
    }
    public static void main(String[] args) {

        int mapNumber = 1;

        TransitionInterface t1 = TransitionBuilder.build(
                FetchFile
                        .lines("resources/FiniteAutomatons/IADPP.wad"));
        TransitionInterface t2 = TransitionBuilder.build(
                FetchFile
                        .lines("resources/FiniteAutomatons/IADPP.wad"));

        FSM f1 = new FSM(t1);
        FSM f2 = new FSM(t2);

        redFM = f1;
        bluFM = f2;
        GameInstance gameInstance = new GameInstance(16, redFM, bluFM);
        gameInstance.run();
    }
}
