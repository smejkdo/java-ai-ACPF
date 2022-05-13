package cz.cvut.fit.smejkdo1.bak.evolution.individual;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.FSM;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionInterface;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.map.GameMap;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;
import cz.cvut.fit.smejkdo1.bak.acpf.windata.MatchWinData;
import cz.cvut.fit.smejkdo1.bak.evolution.fitness.SimpleFitnessComputer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Individual implements IndividualInterface {
    private TransitionInterface genotype;
    private MatchWinData fitness;

    @Override
    public void init(int mutationProbability, InputStyle inputStyle, OutputStyle outputStyle) {
        genotype = TransitionBuilder.build(mutationProbability, inputStyle, outputStyle);
    }

    public Individual() {
    }

    public Individual(TransitionInterface genotype, MatchWinData fitness) {
        this.genotype = genotype;
        this.fitness = fitness;
    }

    public Individual(TransitionInterface genotype) {
        this.genotype = genotype;
    }

    public Individual(Individual individual) {
        this(individual.genotype.deepCopy(), individual.fitness);
    }

    @Override
    public TransitionInterface getGenotype() {
        return genotype;
    }

    @Override
    public void computeFitness(List<Pair<GameMap, List<Pair<Pos, Pos>>>> gameMapData) {
        List<MatchWinData> matchDataList = SimpleFitnessComputer
                .computeForFSM(
                        new FSM(genotype.getInputStyle(), genotype.getOutputStyle(), genotype),
                        gameMapData);
        processFitnessData(matchDataList);
    }

    @Override
    public void processFitnessData(List<MatchWinData> data) {
        fitness = new MatchWinData();
        fitness.setWins(data.stream().mapToInt(MatchWinData::getWins).sum());
        fitness.setLosses(data.stream().mapToInt(MatchWinData::getLosses).sum());
        fitness.setTies(data.stream().mapToInt(MatchWinData::getTies).sum());
        fitness.setL1Score(data.stream().mapToInt(MatchWinData::getL1Score).sum());
        fitness.setL2Score(data.stream().mapToInt(MatchWinData::getL2Score).sum());
        fitness.setL3Score(data.stream().mapToInt(MatchWinData::getL3Score).sum());
        fitness.setMyAgentsOnTarget(data.stream().mapToInt(MatchWinData::getMyAgentsOnTarget).sum());
        fitness.setOpponentAgentsOnTarget(data.stream().mapToInt(MatchWinData::getOpponentAgentsOnTarget).sum());
        fitness.setMyTransition(this.genotype);
    }

    @Override
    public MatchWinData getFitness() {
        return fitness;
    }

    @Override
    public void mutate(int mutationRate) {
        genotype.mutate(mutationRate);
    }

    @Override
    public List<IndividualInterface> crossover(IndividualInterface other) {
        List<TransitionInterface> genotypes = genotype.crossover(other.getGenotype());
        List<IndividualInterface> result = new ArrayList<>();
        result.add(new Individual(genotypes.get(0)));
        result.add(new Individual(genotypes.get(1)));
        return result;
    }

    @Override
    public void repair() {
        genotype.repairSelf();
    }

    @Override
    public IndividualInterface deepCopy() {
        return new Individual(this.genotype.deepCopy(), this.fitness);
    }

    @Override
    public int compare(IndividualInterface other) {
        if (this.fitness.getWins() - this.fitness.getLosses()
                != other.getFitness().getWins() - other.getFitness().getLosses())
            return (this.fitness.getWins() - this.fitness.getLosses())
                    - (other.getFitness().getWins() - other.getFitness().getLosses());
        else if (this.fitness.getMyAgentsOnTarget() != other.getFitness().getMyAgentsOnTarget())
            return this.fitness.getMyAgentsOnTarget() - other.getFitness().getMyAgentsOnTarget();
        else if (this.fitness.getOpponentAgentsOnTarget() != other.getFitness().getOpponentAgentsOnTarget())
            return other.getFitness().getOpponentAgentsOnTarget() - this.fitness.getOpponentAgentsOnTarget(); // lower == better
        else if (this.fitness.getL1Score() != other.getFitness().getL1Score())
            return this.fitness.getL1Score() - other.getFitness().getL1Score();
        else if (this.fitness.getL2Score() != other.getFitness().getL2Score())
            return this.fitness.getL2Score() - other.getFitness().getL2Score();
        else
            return this.fitness.getL3Score() - other.getFitness().getL3Score();

    }

    @Override
    public int dif(IndividualInterface other) {
        return genotype.difference(other.getGenotype());
    }

    @Override
    public boolean equals(IndividualInterface other) {
        return genotype.equals(other.getGenotype());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Individual)) return false;
        Individual that = (Individual) o;
        return Objects.equals(genotype, that.genotype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genotype);
    }

    @Override
    public void setFitness(MatchWinData fitness) {
        this.fitness = fitness;
    }
}
