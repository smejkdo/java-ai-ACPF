package cz.cvut.fit.smejkdo1.bak.acpf.windata;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.util.WinCondition;

public class GameWinData {
    private Team winner;
    private WinCondition winCondition;
    private int score;
    private int winnerAgentsOnTargets;
    private int looserAgentsOnTargets;



    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }

    public WinCondition getWinCondition() {
        return winCondition;
    }

    public void setWinCondition(WinCondition winCondition) {
        this.winCondition = winCondition;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getWinnerAgentsOnTargets() {
        return winnerAgentsOnTargets;
    }

    public void setWinnerAgentsOnTargets(int winnerAgentsOnTargets) {
        this.winnerAgentsOnTargets = winnerAgentsOnTargets;
    }

    public int getLooserAgentsOnTargets() {
        return looserAgentsOnTargets;
    }

    public void setLooserAgentsOnTargets(int looserAgentsOnTargets) {
        this.looserAgentsOnTargets = looserAgentsOnTargets;
    }

    @Override
    public String toString() {
        return "GameWinData{" +
                "\nwinner=" + winner +
                "\n, winCondition=" + winCondition +
                "\n, score=" + score +
                "\n}";
    }

    public String toAnalyze() {
        return winner + " & " + winCondition + " & " + score + " & " + winnerAgentsOnTargets + " & " + looserAgentsOnTargets;
    }

    public GameWinData reverse() {
        GameWinData result = new GameWinData();
        result.winCondition = this.winCondition;
        result.score = this.score;
        result.winnerAgentsOnTargets = this.winnerAgentsOnTargets;
        result.looserAgentsOnTargets = this.looserAgentsOnTargets;
        if (this.winner == Team.RED)
            result.winner = Team.BLU;
        else if (this.winner == Team.BLU)
            result.winner = Team.RED;
        return result;
    }
}
