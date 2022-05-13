package cz.cvut.fit.smejkdo1.bak.acpf.machine.transition.tree.inttree;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyleUtils;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreeRepairList {
    private List<Pair<Integer, Integer>> list = new ArrayList<>();

    public TreeRepairList(InputStyleUtils info) {
        for (int i = 0; i < info.getNumberOfArguments(); i++) {
            list.add(new Pair<>(0, (1 << info.getMaxLengthOfArgumentInBits(i)) - 1));
        }
    }

    public TreeRepairList() {
    }

    public TreeRepairList deepCopy() {
        TreeRepairList result = new TreeRepairList();
        for (int i = 0; i < this.list.size(); i++) {
            result.list.add(new Pair<>(this.list.get(i).getKey(), this.list.get(i).getValue()));
        }

        return result;
    }

    public List<Pair<Integer, Integer>> getList() {
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreeRepairList)) return false;
        TreeRepairList that = (TreeRepairList) o;
        return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    public Pair<Integer, Integer> get(int i) {
        return list.get(i);
    }

    public Pair<Integer, Integer> set(int i, Pair<Integer, Integer> pair) {
        return list.set(i, pair);
    }
}
