package cz.cvut.fit.smejkdo1.bak.evolution.evolution;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationBuilder;
import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationInterface;

public class EvolutionBuilder {
    EvolutionInterface abstractEvolution;

    public EvolutionBuilder() {
        abstractEvolution = new Evolution();
    }

    public EvolutionBuilder(EvolutionInterface abstractEvolution) {
        this.abstractEvolution = abstractEvolution;
    }

    public static EvolutionInterface build(int populationSize,
                                           int mutationProbability,
                                           int catastropheThreshold,
                                           int crossoverProbability,
                                           int maxGenerations) {
        Evolution evolution = new Evolution();
        evolution.setPopulationSize(populationSize);
        evolution.setMutationProbability(mutationProbability);
        evolution.setCatastropheThreshold(catastropheThreshold);
        evolution.setCrossoverProbability(crossoverProbability);
        evolution.setMaxGenerations(maxGenerations);
        return evolution;
    }

    public static EvolutionInterface build(int populationSize,
                                           int mutationProbability,
                                           int catastropheThreshold,
                                           int crossoverProbability,
                                           int maxGenerations,
                                           InputStyle inputStyle,
                                           OutputStyle outputStyle) {
        return new Evolution(
                populationSize,
                mutationProbability,
                catastropheThreshold,
                crossoverProbability,
                maxGenerations,
                inputStyle,
                outputStyle);
    }

    public EvolutionBuilder setPopulationSize(int populationSize) {
        abstractEvolution.setPopulationSize(populationSize);
        return this;
    }

    public EvolutionBuilder setMutationProbability(int mutationProbability) {
        abstractEvolution.setMutationProbability(mutationProbability);
        return this;
    }

    public EvolutionBuilder setCatastropheThreshold(int catastropheThreshold) {
        abstractEvolution.setCatastropheThreshold(catastropheThreshold);
        return this;
    }

    public EvolutionBuilder setCrossoverProbability(int crossoverProbability) {
        abstractEvolution.setCrossoverProbability(crossoverProbability);
        return this;
    }

    public EvolutionBuilder setMaxGenerations(int maxGenerations) {
        abstractEvolution.setMaxGenerations(maxGenerations);
        return this;
    }

    public EvolutionBuilder setPopulation(String folderName) {
        PopulationInterface population = PopulationBuilder.build(folderName);
        abstractEvolution.setPopulation(population);
        return this;
    }

    public EvolutionBuilder setPopulation(PopulationInterface population) {
        abstractEvolution.setPopulation(population);
        return this;
    }

    public EvolutionInterface build() {
        return abstractEvolution;
    }

}
