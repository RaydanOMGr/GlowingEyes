package me.andreasmelone.glowingeyes.common.scheduler;

import me.andreasmelone.glowingeyes.common.exceptions.ToBeImplementedException;

import java.util.HashMap;
import java.util.Map;

public class CodeScheduler implements Scheduler {
    // task, ticks
    Map<Task, Long> tasksToDelay = new HashMap<>();

    @Override
    public Task runLater(Runnable runnable, long ticks) {
        Task task = new CodeTask(runnable);
        tasksToDelay.put(task, ticks);
        return task;
    }

    @Override
    public Task runRepeating(Runnable runnable, long delayTicks, long intervalTicks) {
        throw new ToBeImplementedException();
    }

    @Override
    public void tick() {
        for (Map.Entry<Task, Long> entry : tasksToDelay.entrySet()) {
            Task task = entry.getKey();
            Long ticks = entry.getValue();
            if (ticks <= 0) {
                task.run();
                tasksToDelay.remove(task);
            } else {
                tasksToDelay.replace(task, ticks - 1);
            }
        }
    }
}
