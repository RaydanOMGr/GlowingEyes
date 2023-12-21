package me.andreasmelone.glowingeyes.common.scheduler;

public abstract class ScheduledTask implements Runnable {
    protected long ticksLeft;
    protected long ticksTotal;
    protected boolean isRepeating = false;

    public ScheduledTask runIn(long ticks) {
        this.ticksLeft = ticks;
        this.ticksTotal = ticks;

        return this;
    }

    public ScheduledTask runIn(Scheduler scheduler, long ticks) {
        scheduler.scheduleTask(this.runIn(ticks));

        return this;
    }

    public ScheduledTask runEvery(long ticks) {
        this.ticksLeft = ticks;
        this.ticksTotal = ticks;
        this.isRepeating = true;

        return this;
    }

    public ScheduledTask runEvery(Scheduler scheduler, long ticks) {
        scheduler.scheduleTask(this.runEvery(ticks));

        return this;
    }
}
