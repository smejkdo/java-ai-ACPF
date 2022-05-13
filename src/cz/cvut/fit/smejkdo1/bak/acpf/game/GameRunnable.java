package cz.cvut.fit.smejkdo1.bak.acpf.game;

import cz.cvut.fit.smejkdo1.bak.acpf.Acpf;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.GameWinData;

import java.util.List;

public class GameRunnable implements Runnable {
    private FSM redFSM;
    private FSM bluFSM;
    private GameMap gameMap;
    private List<Pair<Pos, Pos>> targets;
    private GameWinData gameWinData;

    public GameRunnable(FSM redFSM,
                        FSM bluFSM,
                        GameMap gameMap,
                        List<Pair<Pos, Pos>> targets) {
        this.redFSM = redFSM;
        this.bluFSM = bluFSM;
        this.gameMap = gameMap;
        this.targets = targets;
    }

    @Override
    public void run() {
        if (redFSM == null || bluFSM == null || gameMap == null)
            throw new UnsupportedOperationException();
        gameWinData = Acpf.runMap(redFSM, bluFSM, gameMap, targets);

    }
}
