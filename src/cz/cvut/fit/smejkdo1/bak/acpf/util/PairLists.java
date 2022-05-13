package cz.cvut.fit.smejkdo1.bak.acpf.util;

import java.util.ArrayList;
import java.util.List;

public class PairLists {
    public static <A, B> List<Pair<A, B>> buildPair(List<A> first, List<B> second) {
        assert first.size() == second.size();
        List<Pair<A, B>> result = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            result.add(new Pair<>(first.get(i), second.get(i)));
        }
        return result;
    }
}
