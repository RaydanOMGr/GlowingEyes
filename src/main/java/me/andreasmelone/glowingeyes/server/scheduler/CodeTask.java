package me.andreasmelone.glowingeyes.server.scheduler;

public class CodeTask implements Task {
    private final Runnable runnable;
    private boolean cancelled;

    public CodeTask(Runnable runnable) {
        this.runnable = runnable;
    }

    public void run() {
        if (!cancelled) {
            runnable.run();
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        cancelled = true;
    }
}
