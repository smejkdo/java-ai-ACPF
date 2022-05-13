package cz.cvut.fit.smejkdo1.bak.evolution.population;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.Individual;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.IndividualInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PopulationBuilder {
    public static PopulationInterface build(String folderName) {
        File folder = new File("resources/FiniteAutomatons/Evolved/"
                + folderName);
        if (!folder.exists())
            return null;
        PopulationInterface population = new Population();
        List<File> files = Arrays.asList(
                folder.listFiles() == null ? new File[]{} : folder.listFiles());
        if (files.isEmpty())
            return null;

        List<IndividualInterface> individuals = files.parallelStream().map(file -> {
            List<String> list = FetchFile.lines(file);
            TransitionInterface t = TransitionBuilder.build(list);
            return new Individual(t);
        }).collect(Collectors.toList());

        individuals.parallelStream().forEach(IndividualInterface::repair);
        population.setIndividuals(individuals);
        return population;
    }

    /**
     * Loads saved population and if necessary adds newly initialized individuals.
     * @param folderName     name of folder inside "resources/FiniteAutomatons/Evolved/"
     * @param populationSize size of required population
     * @return new population
     */
    public static PopulationInterface build(String folderName, int populationSize) {
        PopulationInterface population = build(folderName);
        if (population == null || population.getIndividuals() == null || population.getIndividuals().isEmpty())
            return null;
        population.setInputStyle(population.getIndividuals().get(0).getGenotype().getInputStyle());

        if (populationSize < population.getIndividuals().size()) {
            population.setIndividuals(population.getIndividuals()
                    .subList(population.getIndividuals().size() - populationSize,
                            population.getIndividuals().size()));
        }

        if (populationSize > population.getIndividuals().size())
            population.getIndividuals()
                    .addAll(build(
                            (populationSize - population.getIndividuals().size()),
                            population.getInputStyle(),
                            population.getIndividuals()
                                    .get(0).getGenotype()
                                    .getOutputStyle()).getIndividuals());
        return population;
    }

    public static PopulationInterface build(int populationSize,
                                            InputStyle inputStyle,
                                            OutputStyle outputStyle) {
        List<IndividualInterface> individuals = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            individuals.add(new Individual());
            individuals.get(individuals.size() - 1)
                    .init((int) (((double) i / (populationSize - 1)) * 100 + 1),
                            inputStyle, outputStyle);
        }
        individuals.parallelStream().forEach(IndividualInterface::repair);
        PopulationInterface population = new Population();
        population.setIndividuals(individuals);
        population.setInputStyle(inputStyle);
        return population;
    }
}
