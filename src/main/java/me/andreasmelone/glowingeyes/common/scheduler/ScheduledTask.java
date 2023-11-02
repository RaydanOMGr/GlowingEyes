package me.andreasmelone.glowingeyes.common.scheduler;

public abstract class ScheduledTask implements Runnable {
    protected long ticksLeft;
    protected long ticksTotal;

    public ScheduledTask runIn(long ticks) {
        this.ticksLeft = ticks;
        this.ticksTotal = ticks;

        return this;
    }

    public ScheduledTask runIn(Scheduler scheduler, long ticks) {
        scheduler.scheduleTask(this.runIn(ticks));

        return this;
    }
}
