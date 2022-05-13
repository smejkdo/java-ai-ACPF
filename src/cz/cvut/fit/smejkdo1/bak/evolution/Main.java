package cz.cvut.fit.smejkdo1.bak.evolution;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.evolution.evolution.EvolutionBuilder;
import cz.cvut.fit.smejkdo1.bak.evolution.evolution.EvolutionInterface;
import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationBuilder;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int populationSize = 400;
        InputStyle inputStyle = InputStyle.COMPLEX_EIGHT_VICINITY;
        OutputStyle outputStyle = OutputStyle.INT_TREE;
        EvolutionInterface evolution = EvolutionBuilder
                .build(
                        populationSize,
                        2,
                        3,
                        30,
                        10000,
                        inputStyle, outputStyle);

        evolution.setEvolutionNumber("80maps_2");
        evolution.setPopulation(PopulationBuilder.build(inputStyle + "/" + outputStyle + "/80maps_newRun/LastGeneration/", populationSize));

        Thread t = new Thread((Runnable) evolution);
        t.start();
        String s;

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                s = scanner.nextLine();
                if (s.equals("exit")) {
                    evolution.finishEvolution();
                    break;
                } else if (s.equals("list"))
                    System.out.println(evolution.listPopulation());

            }
        }
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
