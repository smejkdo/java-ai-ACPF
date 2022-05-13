package cz.cvut.fit.smejkdo1.bak.evolution.individual;

import java.util.Comparator;

public class IndividualComparator implements Comparator<IndividualInterface> {
    @Override
    public int compare(IndividualInterface individualInterface, IndividualInterface t1) {
        return individualInterface.compare(t1);
    }
}
