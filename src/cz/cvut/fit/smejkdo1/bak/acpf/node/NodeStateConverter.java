package cz.cvut.fit.smejkdo1.bak.acpf.node;

public class NodeStateConverter {
    public static String toString(NodeState nodeState){
        switch (nodeState){
            case WALL: return "#";
            case TARGET: return "X";
            default: return " ";
        }
    }
    public static NodeState toNodeState(String string){
        switch (string){
            case " ": return NodeState.EMPTY;
            case "X": return NodeState.TARGET;
            default: return NodeState.WALL;
        }
    }
    public static NodeState toNodeState(char c){
        switch (c){
            case '.':
            case 'S':
            case 'G':
            case ' ':
                return NodeState.EMPTY;
            default:
                return NodeState.WALL;
        }
    }
}
