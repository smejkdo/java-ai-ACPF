package cz.cvut.fit.smejkdo1.bak.evolution.fitness;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.map.LoadMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.FetchFile;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.MatchWinData;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.Individual;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.IndividualComparator;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.IndividualInterface;
import cz.cvut.fit.smejkdo1.bak.evolution.population.Population;
import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BestSolutions {
    private static List<Pair<GameMap, List<Pair<Pos, Pos>>>> gameMapData;
    public static void updateAll() {
        gameMapData = LoadMap.fetchAllTestMapsData();
        saveBestSolutions(searchInEvolvedFolder());
    }

    private static void saveBestSolutions(List<IndividualInterface> bestSolutions) {
        for (IndividualInterface ind :
                bestSolutions) {
            ind.getGenotype()
                    .save(ind.getGenotype().getOutputStyle()
                                    + "_" + ind.getGenotype().getInputStyle() + ".wad",
                            "resources/FiniteAutomatons/Best/");
        }
        Population population = new Population();
        population.setIndividuals(bestSolutions);
        IndividualInterface best = bestOfPopulation(population);
        System.out.println("Best solution is " + best.getGenotype().getOutputStyle()
                + " using " + best.getGenotype().getInputStyle() + " with score: " + best.getFitness().toString());
        population.getBestIndividual().getGenotype()
                .save("BEST" + ".wad",
                        "resources/FiniteAutomatons/Best/");
        bestSolutions.sort(new IndividualComparator());
        bestSolutions.forEach(ind -> System.out
                .println(ind.getGenotype().getOutputStyle()
                        + " using " + ind.getGenotype().getInputStyle()
                        + " with score: " + ind.getFitness().toString()));
    }

    private static List<IndividualInterface> searchInEvolvedFolder() {
        String evolvedFolder = "resources/FiniteAutomatons/Evolved"; //hardcoded path to evolved solutions
        File evolvedFile = new File(evolvedFolder);
        if (!evolvedFile.isDirectory())
            throw new UnsupportedOperationException("File is not a directory.");
        List<IndividualInterface> bestSolutions = new ArrayList<>();
        for (File f :
                Objects.requireNonNull(evolvedFile.listFiles())) {
            bestSolutions.addAll(searchInInputStyleFolders(f));
        }

        return bestSolutions;
    }

    private static List<IndividualInterface> searchInInputStyleFolders(File folder) {
        if (!folder.isDirectory())
            throw new UnsupportedOperationException("File is not a directory.");
        List<IndividualInterface> bestSolutions = new ArrayList<>();
        for (File f :
                Objects.requireNonNull(folder.listFiles())) {
            IndividualInterface ind = searchInOutputStyleFolders(f);
            if (ind != null)
                bestSolutions.add(ind);
        }
        return bestSolutions;
    }

    private static IndividualInterface searchInOutputStyleFolders(File folder) {
        if (!folder.isDirectory())
            throw new UnsupportedOperationException("File is not a directory.");
        List<File> bestSolutions = new ArrayList<>();
        for (File f :
                Objects.requireNonNull(folder.listFiles())) {
            File solution = searchInIterationsFolders(f);
            if (solution != null)
                bestSolutions.add(solution);
        }
        if (bestSolutions.isEmpty())
            return null;


        return bestOfFiles(bestSolutions);
    }

    private static IndividualInterface bestOfPopulation(PopulationInterface population) {
        CentralizedFitnessComputer.compute(population, gameMapData);
        List<MatchWinData> centralizedData = population.getIndividuals().stream()
                .map(IndividualInterface::getFitness).collect(Collectors.toList());
        SimpleFitnessComputer.compute(population, gameMapData);
        for (int i = 0; i < centralizedData.size(); i++) {
            if (!centralizedData
                    .get(i).getMyTransition()
                    .equals(population
                            .getIndividuals()
                            .get(i).getFitness()
                            .getMyTransition()))
                throw new UnsupportedOperationException("Wrong order.");
            population.getIndividuals()
                    .get(i).setFitness(population.getIndividuals()
                    .get(i).getFitness().add(centralizedData.get(i)));
        }
        return population.getBestIndividual();
    }

    private static IndividualInterface bestOfFiles(List<File> files) {
        PopulationInterface population = new Population();
        population.setIndividuals(files.stream().map(file ->
                new Individual(
                        TransitionBuilder.build(
                                FetchFile.lines(file)))
        ).collect(Collectors.toList()));
        return bestOfPopulation(population);
    }

    private static File searchInIterationsFolders(File folder) {
        if (!folder.isDirectory())
            throw new UnsupportedOperationException("File is not a directory.");
        File f = new File(folder.getPath() + "/Best");
        if (f.list() == null)
            return null;
        String[] solutionsNames = f.list();
        if (solutionsNames.length == 0)
            return null;
        Arrays.sort(solutionsNames);
        return new File(f.getPath() + "/" + solutionsNames[solutionsNames.length - 1]);
    }

    public static void main(String[] args) {
        updateAll();
    }
}
