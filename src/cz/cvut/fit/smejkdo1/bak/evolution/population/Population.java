package cz.cvut.fit.smejkdo1.bak.evolution.population;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.TransitionBuilder;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Rand;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.Individual;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.IndividualComparator;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.IndividualInterface;

import java.util.ArrayList;
import java.util.List;

public class Population implements PopulationInterface {
    private List<IndividualInterface> individuals;
    private InputStyle inputStyle;
    private boolean sorted = false;

    @Override
    public List<IndividualInterface> selectIndividuals(int count) {
        int rand;
        List<IndividualInterface> broaderSelection = new ArrayList<>();

        sortIndividualsByFitness();

        for (int i = 0; i < count * 3; i++) {
            rand = Rand.nextInt(this.individuals.size());
            if (rand != 0)
                broaderSelection.add(this.individuals.get(this.individuals.size() - 1 - Rand.nextInt(rand)));
            else
                broaderSelection.add(this.individuals.get(this.individuals.size() - 1));
        }
        while(broaderSelection.size() < count * 4)
            broaderSelection.add(this.individuals.get(Rand.nextInt(this.individuals.size())));

        List<IndividualInterface> actualSelection = new ArrayList<>();
        int idx = Rand.nextInt(broaderSelection.size());

        actualSelection.add(broaderSelection.get(idx));
        broaderSelection.remove(idx);

        while(actualSelection.size() < count) {
            if (broaderSelection.size() == 0)
                actualSelection.add(individuals.get(Rand.nextInt(individuals.size())));
            idx = Rand.nextInt(broaderSelection.size());
            if (!actualSelection.contains(broaderSelection.get(idx)))
                actualSelection.add(broaderSelection.get(idx));
            else
                broaderSelection.remove(idx);
        }
        return actualSelection;
    }

    @Override
    public void sortIndividualsByFitness() {
        if (sorted)
            return;
        individuals.sort(new IndividualComparator());
        sorted = true;
    }

    @Override
    public void apocalypse() {
        if (Rand.nextBoolean())
            apocalypseNew();
        else
            apocalypseOld();
    }

    private void apocalypseNew() {
        int bound = individuals.size() / 3;
        sortIndividualsByFitness();
        individuals.subList(0, bound).parallelStream().forEach(individual -> {
            individual.init(1 + Rand.nextInt(60),
                    inputStyle, getOutputStyle());
            individual.setFitness(null);
        });
        individuals.subList(0, bound).parallelStream().forEach(IndividualInterface::repair);
        sorted = false;
    }

    private void apocalypseOld() {
        int bound = individuals.size() / 3;
        sortIndividualsByFitness();

        for (int i = 0; i < bound; i++) {
            individuals.set(i,
                    new Individual(
                            TransitionBuilder
                                    .build(i / ((bound - 1) == 0 ? 1 : bound - 1) + 1,
                                            inputStyle,
                                            getBestIndividual()
                                                    .getGenotype()
                                                    .getOutputStyle())));
        }
        individuals.subList(0, bound).parallelStream().forEach(IndividualInterface::repair);
        sorted = false;
    }

    public IndividualInterface getBestIndividual() {
        sortIndividualsByFitness();
        return individuals.get(individuals.size() - 1);
    }

    @Override
    public String listPopulation() {
        StringBuilder sb = new StringBuilder();
        for (IndividualInterface ind : individuals) {
            sb.append(ind.getFitness());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void setIndividuals(List<IndividualInterface> individuals) {
        this.individuals = individuals;
        sorted = false;
    }

    @Override
    public void setIndividualAt(int index, IndividualInterface individual) {
        this.individuals.set(index, individual);
        sorted = false;
    }

    @Override
    public List<IndividualInterface> getIndividuals() {
        return individuals;
    }

    @Override
    public void setInputStyle(InputStyle inputStyle) {
        this.inputStyle = inputStyle;
    }

    @Override
    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    @Override
    public InputStyle getInputStyle() {
        return inputStyle;
    }

    @Override
    public OutputStyle getOutputStyle() {
        return individuals.get(0).getGenotype().getOutputStyle();
    }
}
