package me.andreasmelone.glowingeyes.common.scheduler;

public class CodeTask implements Task {
    private final Runnable runnable;
    private boolean cancelled;

    public CodeTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        if (!this.cancelled) {
            this.runnable.run();
        }
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    @Override
    public void cancel() {
        this.cancelled = true;
    }
}
