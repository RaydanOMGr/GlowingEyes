package me.andreasmelone.glowingeyes.fabric.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class EyesCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> eyes = literal("eyes");
        eyes.executes(context -> {
            GlowingEyes.SCHEDULER_CLIENT.runLater(
                    () -> context.getSource().getClient().setScreen(new EyesEditorScreen()),
                    1L
            );
            return 1;
        });
        dispatcher.register(eyes);
    }
}
