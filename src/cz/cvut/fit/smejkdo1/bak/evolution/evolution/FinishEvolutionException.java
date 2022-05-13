package cz.cvut.fit.smejkdo1.bak.evolution.evolution;

public class FinishEvolutionException extends Throwable {
    private String message;
    public FinishEvolutionException(String message){
        this.message = message;
    }

    public void printMessage() {
        System.out.println(message);
    }
}
