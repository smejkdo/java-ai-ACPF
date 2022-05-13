package cz.cvut.fit.smejkdo1.bak.evolution.evolution;

import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationInterface;

public interface EvolutionInterface {
    void setPopulationSize(int populationSize); //set population size of
    void setMutationProbability(int mutationProbability);
    void setCatastropheThreshold(int catastropheThreshold);
    void setPopulation(PopulationInterface population);
    void finishEvolution();
    void setCrossoverProbability(int crossoverProbability);
    void setMaxGenerations(int maxGenerations);

    void setEvolutionNumber(String evolutionNumber);

    void run();

    String listPopulation();
}
