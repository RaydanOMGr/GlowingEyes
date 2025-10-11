package me.andreasmelone.glowingeyes.common.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CodeScheduler implements Scheduler {
    // task, ticks
    Map<Task, Long> tasksToDelay = new ConcurrentHashMap<>();
    Map<Task, DoubleLong> tasksToRepeat = new ConcurrentHashMap<>();

    @Override
    public Task runLater(Runnable runnable, long ticks) {
        Task task = new CodeTask(runnable);
        this.tasksToDelay.put(task, ticks);
        return task;
    }

    @Override
    public Task runRepeating(Runnable runnable, long delayTicks, long intervalTicks) {
        Task task = new CodeTask(runnable);
        this.tasksToRepeat.put(task, new DoubleLong(delayTicks, intervalTicks));
        return task;
    }

    @Override
    public void tick() {
        for (Map.Entry<Task, Long> entry : this.tasksToDelay.entrySet()) {
            Task task = entry.getKey();
            Long ticks = entry.getValue();
            if (ticks <= 0) {
                task.run();
                this.tasksToDelay.remove(task);
            } else {
                this.tasksToDelay.replace(task, ticks - 1);
            }
        }
        for (Map.Entry<Task, DoubleLong> entry : this.tasksToRepeat.entrySet()) {
            Task task = entry.getKey();
            DoubleLong doubleLong = entry.getValue();
            long delayTicks = doubleLong.delay;
            long intervalTicks = doubleLong.interval;
            if (delayTicks <= 0) {
                task.run();
                this.tasksToRepeat.replace(task, new DoubleLong(intervalTicks, intervalTicks));
            } else {
                this.tasksToRepeat.replace(task, new DoubleLong(delayTicks - 1, intervalTicks));
            }
        }
    }

    private record DoubleLong(long delay, long interval) {
    }
}
