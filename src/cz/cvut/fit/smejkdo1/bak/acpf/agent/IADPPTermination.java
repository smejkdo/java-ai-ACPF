package cz.cvut.fit.smejkdo1.bak.acpf.agent;


import java.util.concurrent.atomic.AtomicInteger;

public class IADPPTermination {
    private final Object lock = new Lock();
    private static int lockedThreads = 0;
    private AtomicInteger[] agentsRunning;
    private AtomicInteger numOfRunningAgents;

    public IADPPTermination(int numOfAgents) {
        agentsRunning = new AtomicInteger[numOfAgents];
        for (int i = 0; i < agentsRunning.length; i++) {
            agentsRunning[i] = new AtomicInteger(0);
        }
        numOfRunningAgents = new AtomicInteger(numOfAgents);
    }

    public void lockThread() {
        synchronized (lock) {
            while (!isIterationTerminated()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void lockBeforeRun() {
        numOfRunningAgents.decrementAndGet();
        synchronized (lock) {
            if (numOfRunningAgents.get() == 0) {
                lock.notifyAll();
            } else {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void markAsRunning(int id) {
        if (agentsRunning[id].get() == 0) {
            numOfRunningAgents.incrementAndGet();
        }
        agentsRunning[id].incrementAndGet();
    }

    public void markAsTerminated(int id) {
        if (agentsRunning[id].get() == 1) {
            numOfRunningAgents.decrementAndGet();
        }
        agentsRunning[id].decrementAndGet();

        if (isIterationTerminated())
            synchronized (lock) {
                lock.notifyAll();
            }
    }

    private boolean isIterationTerminated() {
        for (int i = 0; i < agentsRunning.length; i++) {
            if (agentsRunning[i].get() != 0)
                return false;
        }
        return true;
        //return numOfRunningAgents.get() == 0;
    }

    public void markAsIdle(int id) {
        numOfRunningAgents.decrementAndGet();
        initializationWakeUpFromIdle();
    }

    public void initializationWakeUpFromIdle() {
        synchronized (lock) {
            if (numOfRunningAgents.get() == 0) {
                lock.notifyAll();
            }
        }
    }

    private static final class Lock {
    }

}
