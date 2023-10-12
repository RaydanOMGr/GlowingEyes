package me.andreasmelone.glowingeyes.client.commands;

import com.google.common.collect.Lists;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.data.DataSaveFile;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.ClientCapabilityMessage;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
        if(GlowingEyes.serverHasMod) {
            if (!(sender instanceof EntityPlayer)) {
                sender.sendMessage(format(TextFormatting.RED, "Only players can use this command!"));
                return;
            }
            EntityPlayer player = (EntityPlayer) sender;
            IGlowingEyesCapability cap = new GlowingEyesCapability();

            if (args.length < 1) {
                sender.sendMessage(new TextComponentString("Eyes enabled: " + cap.hasGlowingEyes()));
                sender.sendMessage(new TextComponentString("Eyes type:    " + cap.getGlowingEyesType()));
                sender.sendMessage(new TextComponentString(""));
                sender.sendMessage(new TextComponentString("Usage: /eyes <toggle | type> <arguments>"));
                return;
            }

            if (args[0].equalsIgnoreCase("toggle")) {
                boolean toggleValue = false;

                if (args.length == 2) {
                    if ((args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("on"))) {
                        toggleValue = true;
                    } else if ((args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("off"))) {
                        toggleValue = false;
                    } else {
                        sender.sendMessage(format(TextFormatting.RED, "Invalid toggle value!"));
                        sender.sendMessage(format(TextFormatting.RED, "Usage: /eyes <toggle | type> <arguments>"));
                        return;
                    }
                }

                cap.setHasGlowingEyes(toggleValue);
                NetworkHandler.sendToServer(new ClientCapabilityMessage(cap, player));

                sender.sendMessage(format(TextFormatting.GREEN, "Your glowing eyes are now " + (toggleValue ? "enabled" : "disabled") + "!"));
            } else if (args[0].equalsIgnoreCase("type")) {
                int type;
                try {
                    type = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(format(TextFormatting.RED, "Invalid type!"));
                    sender.sendMessage(format(TextFormatting.RED, "Usage: /eyes <toggle | type> <arguments>"));
                    return;
                }

                if (type < 0 || type > ModInfo.EYE_TYPE_COUNT - 1) {
                    type = 0;
                }

                cap.setHasGlowingEyes(true);
                cap.setGlowingEyesType(type);

                NetworkHandler.sendToServer(new ClientCapabilityMessage(cap, player));
                sender.sendMessage(format(TextFormatting.GREEN, "Set your glowing eyes type to " + type + "!"));
            }
        } else {
            if (!(sender instanceof EntityPlayer)) {
                sender.sendMessage(format(TextFormatting.RED, "Only players can use this command!"));
                return;
            }
            DataSaveFile dataSaveFile = GlowingEyes.proxy.getDataSaveFile();

            if (args.length < 1) {
                sender.sendMessage(new TextComponentString("Eyes enabled: " + dataSaveFile.getHasGlowingEyes()));
                sender.sendMessage(new TextComponentString("Eyes type:    " + dataSaveFile.getGlowingEyesType()));
                sender.sendMessage(new TextComponentString(""));
                sender.sendMessage(new TextComponentString("Usage: /eyes <toggle | type> <arguments>"));
                return;
            }

            if (args[0].equalsIgnoreCase("toggle")) {
                boolean toggleValue = false;

                if (args.length == 2) {
                    if ((args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("on"))) {
                        toggleValue = true;
                    } else if ((args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("off"))) {
                        toggleValue = false;
                    } else {
                        sender.sendMessage(format(TextFormatting.RED, "Invalid toggle value!"));
                        sender.sendMessage(format(TextFormatting.RED, "Usage: /eyes <toggle | type> <arguments>"));
                        return;
                    }
                }

                dataSaveFile.setHasGlowingEyes(toggleValue);

                sender.sendMessage(format(TextFormatting.GREEN, "Your glowing eyes are now " + (toggleValue ? "enabled" : "disabled") + "!"));
            } else if (args[0].equalsIgnoreCase("type")) {
                int type;
                try {
                    type = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(format(TextFormatting.RED, "Invalid type!"));
                    sender.sendMessage(format(TextFormatting.RED, "Usage: /eyes <toggle | type> <arguments>"));
                    return;
                }

                if (type < 0 || type > ModInfo.EYE_TYPE_COUNT - 1) {
                    type = 0;
                }

                dataSaveFile.setHasGlowingEyes(true);
                dataSaveFile.setGlowingEyesType(type);

                sender.sendMessage(format(TextFormatting.GREEN, "Set your glowing eyes type to " + type + "!"));
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    // This does not work and idk why, if anyone knows please tell me
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 0) {
            List<String> list = new ArrayList<>();
            list.add("toggle");
            list.add("type");
            return list;
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("toggle")) {
                List<String> list = new ArrayList<>();
                list.add("on");
                list.add("off");
                return list;
            } else if(args[0].equalsIgnoreCase("type")) {
                List<String> list = new ArrayList<>();
                for(int i = 0; i < 16; i++) {
                    list.add(String.valueOf(i));
                }
                return list;
            } else {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    private TextComponentTranslation format(TextFormatting color, String str, Object... args) {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(color);
        return ret;
    }
}