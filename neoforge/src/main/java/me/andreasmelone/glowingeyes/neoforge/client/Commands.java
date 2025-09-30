package me.andreasmelone.glowingeyes.neoforge.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.andreasmelone.glowingeyes.client.commands.EyesCommand;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

public class Commands {
    private final ClientModContext mod;

    public Commands(ClientModContext mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterClientCommandsEvent event) {
        new EyesCommand<>(Commands::createArgumentBuilder).register(mod, event.getDispatcher());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createArgumentBuilder(String name) {
        return net.minecraft.commands.Commands.literal(name);
    }
}
