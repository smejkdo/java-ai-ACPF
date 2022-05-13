package cz.cvut.fit.smejkdo1.bak.acpf.windata;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.util.WinCondition;

import java.util.List;

public class MatchWinData {
    private int wins = 0;
    private int losses = 0;
    private int ties = 0;
    private int l1Score = 0;
    private int l2Score = 0;
    private int l3Score = 0;
    private int myAgentsOnTarget = 0;
    private int opponentAgentsOnTarget = 0;
    private TransitionInterface myTransition;
    private TransitionInterface opponentTransition;

    public MatchWinData(List<GameWinData> list,
                        TransitionInterface myTransition,
                        TransitionInterface opponentTransition) {
        this(list);
        this.myTransition = myTransition;
        this.opponentTransition = opponentTransition;
    }

    public MatchWinData(){}

    public MatchWinData(List<GameWinData> list){
        for (GameWinData data : list) {
            if (data.getWinner() == Team.RED){
                wins++;
                addScore(data.getWinCondition(), data.getScore());
                myAgentsOnTarget += data.getWinnerAgentsOnTargets();
                opponentAgentsOnTarget += data.getLooserAgentsOnTargets();
            } else if (data.getWinner() == Team.BLU) {
                losses++;
                addScore(data.getWinCondition(), -data.getScore());
                opponentAgentsOnTarget += data.getWinnerAgentsOnTargets();
                myAgentsOnTarget += data.getLooserAgentsOnTargets();
            } else ties++;

        }
    }

    public MatchWinData(int wins, int losses,
                        int ties, int l1Score,
                        int l2Score, int l3Score,
                        int myAgentsOnTarget, int opponentAgentsOnTarget,
                        TransitionInterface myTransition,
                        TransitionInterface opponentTransition) {
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
        this.l1Score = l1Score;
        this.l2Score = l2Score;
        this.l3Score = l3Score;
        this.myAgentsOnTarget = myAgentsOnTarget;
        this.opponentAgentsOnTarget = opponentAgentsOnTarget;
        this.myTransition = myTransition;
        this.opponentTransition = opponentTransition;
    }

    private void addScore(WinCondition winCondition, int score){
        if (winCondition.equals(WinCondition.ALL_ON_TARGET))
            l1Score += score;
        else if (winCondition.equals(WinCondition.MORE_ON_TARGET))
            l2Score += score;
        else if (winCondition.equals(WinCondition.CLOSER_TO_TARGET))
            l3Score += score;
        else throw new UnsupportedOperationException("Unsupported option occurred. Option: "
                    + winCondition + " occurred.");
    }

    public MatchWinData reverse() {
        return new MatchWinData(losses, wins, ties,
                -l1Score, -l2Score, -l3Score,
                opponentAgentsOnTarget, myAgentsOnTarget,
                opponentTransition, myTransition);
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getL1Score() {
        return l1Score;
    }

    public void setL1Score(int l1Score) {
        this.l1Score = l1Score;
    }

    public int getL2Score() {
        return l2Score;
    }

    public void setL2Score(int l2Score) {
        this.l2Score = l2Score;
    }

    public int getL3Score() {
        return l3Score;
    }

    public void setL3Score(int l3Score) {
        this.l3Score = l3Score;
    }

    public int getTies() {
        return ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    public int getMyAgentsOnTarget() {
        return myAgentsOnTarget;
    }

    public void setMyAgentsOnTarget(int myAgentsOnTarget) {
        this.myAgentsOnTarget = myAgentsOnTarget;
    }

    public int getOpponentAgentsOnTarget() {
        return opponentAgentsOnTarget;
    }

    public void setOpponentAgentsOnTarget(int opponentAgentsOnTarget) {
        this.opponentAgentsOnTarget = opponentAgentsOnTarget;
    }

    public TransitionInterface getMyTransition() {
        return myTransition;
    }

    public void setMyTransition(TransitionInterface myTransition) {
        this.myTransition = myTransition;
    }

    public TransitionInterface getOpponentTransition() {
        return opponentTransition;
    }

    public void setOpponentTransition(TransitionInterface opponentTransition) {
        this.opponentTransition = opponentTransition;
    }

    @Override
    public String toString() {
        return "{" +
                "W=" + wins +
                ", L=" + losses +
                ", T=" + ties +
                ", A=" + myAgentsOnTarget +
                '}';
    }

    public MatchWinData add(MatchWinData oth) {
        MatchWinData result = new MatchWinData();
        result.wins = this.wins + oth.wins;
        result.losses = this.losses + oth.losses;
        result.ties = this.ties + oth.ties;
        result.l1Score = this.l1Score + oth.l1Score;
        result.l2Score = this.l2Score + oth.l2Score;
        result.l3Score = this.l3Score + oth.l3Score;
        result.myAgentsOnTarget = this.myAgentsOnTarget + oth.myAgentsOnTarget;
        result.opponentAgentsOnTarget = this.opponentAgentsOnTarget + oth.opponentAgentsOnTarget;
        result.myTransition = this.myTransition;
        result.opponentTransition = this.opponentTransition;
        return result;
    }

    public void toCSV(StringBuilder sb) {
        sb.append(wins).append(";")
                .append(losses).append(";")
                .append(ties).append(";")
                .append(myAgentsOnTarget).append(";")
                .append(opponentAgentsOnTarget).append(";")
                .append(l1Score).append(";")
                .append(l2Score).append(";")
                .append(l3Score).append("\n");
    }

    public String toCSV() {
        return wins + ";" +
                losses + ";" +
                ties + ";" +
                myAgentsOnTarget + ";" +
                opponentAgentsOnTarget + ";" +
                l1Score + ";" +
                l2Score + ";" +
                l3Score + "\n";
    }

    public boolean isPositive() {
        return wins > losses && myAgentsOnTarget > opponentAgentsOnTarget;
    }

    public boolean isNotNegative() {
        return wins >= losses && myAgentsOnTarget >= opponentAgentsOnTarget;
    }
}
