package me.andreasmelone.glowingeyes.client.commands;

import com.google.common.collect.Lists;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.ui.EyesEditScreen;
import me.andreasmelone.glowingeyes.common.scheduler.ScheduledTask;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class EyesCommand implements ICommand {

    @Override
    public int compareTo(ICommand arg0) {
        return 0;
    }

    @Override
    public String getName() {
        return "eyes";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/eyes <toggle | type> <arguments>";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = Lists.newArrayList();
        aliases.add("/eyes");
        aliases.add("/eye");
        aliases.add("/glowingeyes");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        GlowingEyes.proxy.getScheduler().scheduleTask(new ScheduledTask() {
            @Override
            public void run() {
                GlowingEyes.proxy.openGui(new EyesEditScreen(Minecraft.getMinecraft()));
            }
        }.runIn(1));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}