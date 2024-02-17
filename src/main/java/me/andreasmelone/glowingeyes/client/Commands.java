package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.client.commands.EyesCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Commands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        EyesCommand.register(event.getDispatcher());
    }
}
