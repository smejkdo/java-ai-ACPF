package cz.cvut.fit.smejkdo1.bak.evolution.evolution;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.map.LoadMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.NumberPadding;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;
import cz.cvut.fit.smejkdo1.bak.evolution.fitness.SimpleFitnessComputer;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.IndividualComparator;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.IndividualInterface;
import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationBuilder;
import cz.cvut.fit.smejkdo1.bak.evolution.population.PopulationInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Evolution implements EvolutionInterface, Runnable {

    private String evolutionNumber = "0003";
    private static final int NUM_OF_THREADS = 12;
    private static final int NUM_OF_EVO_MAPS = 80;
    private int populationSize;
    private int mutationProbability;
    private int catastropheThreshold;
    private final int mapChangeThreshold = 2;
    private final int agentsChangeThreshold = 1;
    private final List<IndividualInterface> bestIndividuals = new ArrayList<>();
    private int crossoverProbability;
    private int maxGenerations;
    private InputStyle inputStyle = InputStyle.INFORMED_VICINITY;
    private OutputStyle outputStyle = OutputStyle.TRANSITION_LIST;
    private PopulationInterface population;
    private boolean isTerminated = false;
    private int catastrophe = 0;
    private int mapChange = 0;
    private int agentsChange = 0;

    private List<Pair<GameMap, List<Pair<Pos, Pos>>>> allGameMapData;
    private List<Pair<GameMap, List<Pair<Pos, Pos>>>> evolutionGameMapData;

    public Evolution() {
    }

    public Evolution(int populationSize,
                     int mutationProbability,
                     int catastropheThreshold,
                     int crossoverProbability,
                     int maxGenerations) {
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        this.catastropheThreshold = catastropheThreshold;
        this.crossoverProbability = crossoverProbability;
        this.maxGenerations = maxGenerations;
    }

    public Evolution(int populationSize,
                     int mutationProbability,
                     int catastropheThreshold,
                     int crossoverProbability,
                     int maxGenerations,
                     PopulationInterface population) {
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        this.catastropheThreshold = catastropheThreshold;
        this.crossoverProbability = crossoverProbability;
        this.maxGenerations = maxGenerations;
        this.population = population;
    }

    public Evolution(int populationSize,
                     int mutationProbability,
                     int catastropheThreshold,
                     int crossoverProbability,
                     int maxGenerations,
                     InputStyle inputStyle,
                     OutputStyle outputStyle) {
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        this.catastropheThreshold = catastropheThreshold;
        this.crossoverProbability = crossoverProbability;
        this.maxGenerations = maxGenerations;
        this.inputStyle = inputStyle;
        this.outputStyle = outputStyle;
    }


    @Override
    public void run() {
        prepareEvolution();
        try {
            mainCycle();
        } catch (FinishEvolutionException e) {
            e.printMessage();
        }
    }

    @Override
    public String listPopulation() {
        return population.listPopulation();
    }

    private void prepareEvolution() {
        allGameMapData = LoadMap.fetchAllEvolutionMapData();
        changeEvolutionMaps();
        if (population == null) {
            population = PopulationBuilder.build(populationSize, inputStyle, outputStyle);
        }
        SimpleFitnessComputer.compute(population, evolutionGameMapData);
    }

    private void changeEvolutionMaps() {
        //if (!population.getBestIndividual().getFitness().isNotNegative()) return;
        agentsChange++;
        if (agentsChange >= agentsChangeThreshold) {
            allGameMapData = LoadMap.fetchAllEvolutionMapData();
            agentsChange = 0;
        }
        SimpleFitnessComputer.changeActiveComparators(); //change active oponents for fitness computation
        Collections.shuffle(allGameMapData);
        evolutionGameMapData = allGameMapData.subList(0, Math.min(NUM_OF_EVO_MAPS, allGameMapData.size()));
        System.out.println("New chosen maps: {");
        for (Pair<GameMap, List<Pair<Pos, Pos>>> mapData :
                evolutionGameMapData) {
            System.out.println(mapData.getKey().getMapSize().x + "x"
                    + mapData.getKey().getMapSize().y + " with "
                    + (mapData.getValue().size() - 1) + " agents.");
        }
        System.out.println("};");
    }

    private void mainCycle() throws FinishEvolutionException {
        for (int i = 0; i < maxGenerations; i++) {
            if (isTerminated)
                throw new FinishEvolutionException("Evolution was terminated from outside.");
            isCatastrophe();
            createNewPopulation();
            System.out.println("GEN: " + i);
            recordNewGeneration();
        }
        finishEvolution();
        throw new FinishEvolutionException("Evolution finished all required generations.");
    }

    private void recordNewGeneration() {
        bestIndividuals.add(population.getBestIndividual());
        saveLastBestIndividual();
        if (bestIndividuals.size() > 1
                && bestIndividuals.get(bestIndividuals.size() - 1)
                .compare(bestIndividuals.get(bestIndividuals.size() - 2)) <= 0)
            catastrophe++;
        else
            catastrophe = 0;
        System.out.println("BEST: " + population.getBestIndividual().getFitness());
        System.out.println("WORST: " + population.getIndividuals().get(0).getFitness());
    }

    private void createNewPopulation() throws FinishEvolutionException {
        List<IndividualInterface> newIndividuals = Collections.synchronizedList(new ArrayList<>());
        //newIndividuals.add(population.getBestIndividual());

        //Multithreaded version
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUM_OF_THREADS; i++) {
            Thread t = new Thread(() -> {
                while (newIndividuals.size() < populationSize) {
                    if (isTerminated)
                        return;
                    List<IndividualInterface> newInds = newGenerationIndividuals();
                    for (IndividualInterface ind : newInds) {
                        if (newIndividuals.size() < populationSize
                                && !newIndividuals.contains(ind))
                            newIndividuals.add(ind);
                    }
                }
            });
            threads.add(t);
        }
        population.sortIndividualsByFitness();
        threads.forEach(Thread::start);
        try {
            for (Thread thread : threads) thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isTerminated)
            throw new FinishEvolutionException("Evolution was terminated from outside.");

        /*
        //SingleThread version
        while (newIndividuals.size() < populationSize) {

            List<IndividualInterface> newInds = newGenerationIndividuals();
            for (IndividualInterface ind : newInds) {
                if (newIndividuals.size() < populationSize
                        && !newIndividuals.contains(ind))
                    newIndividuals.add(ind);
            }
        }*/

        population.setIndividuals(newIndividuals);
        //SimpleFitnessComputer.compute(population);
    }

    private List<IndividualInterface> newGenerationIndividuals() {
        List<IndividualInterface> parents = population.selectIndividuals(2);
        List<IndividualInterface> children = new ArrayList<>();

        if (Rand.nextInt(100) < crossoverProbability) {
            children = parents.get(0).deepCopy().crossover(parents.get(1).deepCopy());
        } else {
            children.add(parents.get(0).deepCopy());
            children.add(parents.get(1).deepCopy());
        }

        children.forEach(ind -> ind.mutate(mutationProbability));
        children.forEach(IndividualInterface::repair);
        children = deterministicCrowding(children, parents);

        return children;
    }

    private void isCatastrophe() {
        if (catastrophe >= catastropheThreshold) {
            System.out.println("CATASTROPHE");
            population.apocalypse();
            catastrophe = 0;
            mapChange++;
            if (mapChange >= mapChangeThreshold) {
                changeEvolutionMaps();
                mapChange = 0;
            }
            SimpleFitnessComputer.compute(population, evolutionGameMapData);
        }
    }

    private List<IndividualInterface> deterministicCrowding(List<IndividualInterface> children, List<IndividualInterface> parents) {
        try {
            children.forEach(ind -> ind.computeFitness(evolutionGameMapData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<IndividualInterface> both = new ArrayList<>(children);
        both.addAll(parents);
        both.sort(new IndividualComparator());
        parents.forEach(parent -> {
            if (both.indexOf(parent) >= parents.size())
                children.add(parent);
        });
        return children;
    }

    private void saveLastBestIndividual() {
        String filePath = "resources/FiniteAutomatons/Evolved/"
                + population.getInputStyle() + "/"
                + population.getOutputStyle() + "/"
                + evolutionNumber + "/Best/";
        population.getBestIndividual().getGenotype()
                .save(NumberPadding.intPadding(bestIndividuals.size() - 1, 5) + ".wad", filePath);
    }

    private void saveBestIndividuals() {
        String filePath = "resources/FiniteAutomatons/Evolved/"
                + population.getInputStyle() + "/"
                + population.getOutputStyle() + "/"
                + evolutionNumber + "/Best/";
        if (bestIndividuals.isEmpty()) {
            population.getBestIndividual().getGenotype()
                    .save(NumberPadding.intPadding(0, 5) + ".wad", filePath);
            return;
        }
        for (int i = 0; i < bestIndividuals.size(); i++) {
            bestIndividuals.get(i).getGenotype()
                    .save(NumberPadding.intPadding(i, 5) + ".wad", filePath);
        }

    }

    @Override
    public void finishEvolution() {
        isTerminated = true;
        //saveBestIndividuals();
        saveLastGeneration();
        System.out.println("Evolution number " + evolutionNumber + " finished and was saved.");
    }

    private void saveLastGeneration() {
        String filePath = "resources/FiniteAutomatons/Evolved/"
                + population.getInputStyle() + "/"
                + population.getIndividuals().get(0).getGenotype().getOutputStyle() + "/"
                + evolutionNumber + "/LastGeneration/";
        for (int i = 0; i < population.getIndividuals().size(); i++) {
            population.getIndividuals()
                    .get(population.getIndividuals().size() - 1 - i)
                    .getGenotype()
                    .save(NumberPadding.intPadding(i, 5) + ".wad", filePath);
        }
    }

    @Override
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public void setMutationProbability(int mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    @Override
    public void setCatastropheThreshold(int catastropheThreshold) {
        this.catastropheThreshold = catastropheThreshold;
    }

    @Override
    public void setCrossoverProbability(int crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    @Override
    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    @Override
    public void setPopulation(PopulationInterface population) {
        this.population = population;
    }

    public void setInputStyle(InputStyle inputStyle) {
        this.inputStyle = inputStyle;
    }

    public void setOutputStyle(OutputStyle outputStyle) {
        this.outputStyle = outputStyle;
    }

    public void setEvolutionNumber(String evolutionNumber) {
        this.evolutionNumber = evolutionNumber;
    }
}
