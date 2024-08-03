package me.andreasmelone.glowingeyes.forge.client;

import me.andreasmelone.glowingeyes.forge.client.commands.EyesCommand;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Commands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        EyesCommand.register(event.getDispatcher());
    }
}
