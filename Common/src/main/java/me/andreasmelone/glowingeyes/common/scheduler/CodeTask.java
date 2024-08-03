package me.andreasmelone.glowingeyes.common.scheduler;

public class CodeTask implements Task {
    private final Runnable runnable;
    private boolean cancelled;

    public CodeTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        if (!cancelled) {
            runnable.run();
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    @Override
    public void cancel() {
        cancelled = true;
    }
}
