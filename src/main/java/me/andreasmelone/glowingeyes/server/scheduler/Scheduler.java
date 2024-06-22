package me.andreasmelone.glowingeyes.server.scheduler;

public interface Scheduler {
    Task runLater(Runnable runnable, long ticks);
    Task runRepeating(Runnable runnable, long delayTicks, long intervalTicks);
    void tick();
}
