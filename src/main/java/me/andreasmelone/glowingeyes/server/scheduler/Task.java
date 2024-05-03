package me.andreasmelone.glowingeyes.server.scheduler;

public interface Task {
    void run();
    boolean isCancelled();
    void cancel();
}
