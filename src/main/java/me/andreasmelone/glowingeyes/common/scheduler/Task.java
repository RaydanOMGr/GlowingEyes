package me.andreasmelone.glowingeyes.common.scheduler;

public interface Task {
    void run();
    boolean isCancelled();
    void cancel();
}
