package me.andreasmelone.glowingeyes.common.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    List<ScheduledTask> tasks;

    public Scheduler() {
        this.tasks = new ArrayList<>();
    }

    public void tick() {
        List<ScheduledTask> queue = new ArrayList<>();
        for(ScheduledTask task : tasks) {
            task.ticksLeft -= 1;
            if(task.ticksLeft <= 0) {
                task.run();
                if(task.isRepeating) {
                    task.ticksLeft = task.ticksTotal;
                } else {
                    queue.add(task);
                }
            }
        }
        tasks.removeAll(queue);
        queue.clear();
    }

    public void scheduleTask(ScheduledTask task) {
        tasks.add(task);
    }
}
