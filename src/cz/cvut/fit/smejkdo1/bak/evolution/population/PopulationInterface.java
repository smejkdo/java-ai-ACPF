package cz.cvut.fit.smejkdo1.bak.evolution.population;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;
import cz.cvut.fit.smejkdo1.bak.evolution.individual.IndividualInterface;

import java.util.List;

public interface PopulationInterface {
    List<IndividualInterface> individuals = null;

    List<IndividualInterface> selectIndividuals(int count);

    void apocalypse();

    void sortIndividualsByFitness();

    void setIndividuals(List<IndividualInterface> individuals);

    void setIndividualAt(int index, IndividualInterface individual);

    void setInputStyle(InputStyle inputStyle);

    void setSorted(boolean sorted);

    List<IndividualInterface> getIndividuals();

    InputStyle getInputStyle();

    IndividualInterface getBestIndividual();

    String listPopulation();

    OutputStyle getOutputStyle();
}
