package me.andreasmelone.glowingeyes;

import me.andreasmelone.glowingeyes.common.scheduler.CodeScheduler;
import me.andreasmelone.glowingeyes.common.scheduler.Scheduler;

import java.awt.*;

// this is basically the Constants class
public class GlowingEyes {
	public static final String MOD_ID = "glowingeyes";
	public static final String MOD_NAME = "Glowing Eyes";
	public static final Color DEFAULT_COLOR = new Color(242, 0, 0, 210);
	public static final Scheduler SCHEDULER_SERVER = new CodeScheduler();
	public static final Scheduler SCHEDULER_CLIENT = new CodeScheduler();
}
