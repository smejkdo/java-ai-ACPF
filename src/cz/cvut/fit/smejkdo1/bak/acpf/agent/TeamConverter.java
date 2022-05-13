package cz.cvut.fit.smejkdo1.bak.acpf.agent;

public class TeamConverter {
    public static String toString(Team team){
        switch (team){
            case BLU: return "B";
            case RED: return "R";
            default: return "N";
        }
    }
    public static char toChar(Team team){
        switch (team){
            case BLU: return 'B';
            case RED: return 'R';
            default: return 'N';
        }
    }
    public static Team toTeam(char c){
        switch (c){
            case 'B': return Team.BLU;
            case 'R': return Team.RED;
            default: return Team.NONE;
        }
    }
}
