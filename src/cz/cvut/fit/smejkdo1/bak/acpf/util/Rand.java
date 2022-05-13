package cz.cvut.fit.smejkdo1.bak.acpf.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Rand {
    public static Random random = new Random(42);

    public static int nextInt(int bound) {
        return random().nextInt(bound);
    }

    public static int nextInt() {
        return random().nextInt();
    }

    public static boolean nextBoolean() {
        return random().nextBoolean();
    }

    public static double nextDouble() {
        return random().nextDouble();
    }

    private static Random random() {
        //return random;
        return ThreadLocalRandom.current();
    }
}
