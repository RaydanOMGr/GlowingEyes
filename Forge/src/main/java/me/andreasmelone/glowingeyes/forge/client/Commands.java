package me.andreasmelone.glowingeyes.forge.client;

import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.forge.client.commands.EyesCommand;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Commands {
    private ClientModContext mod;

    public Commands(ClientModContext mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterClientCommandsEvent event) {
        EyesCommand.register(mod, event.getDispatcher());
    }
}
