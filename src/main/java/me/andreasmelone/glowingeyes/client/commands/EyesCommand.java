package me.andreasmelone.glowingeyes.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.andreasmelone.glowingeyes.client.GlowingEyesClient;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class EyesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> eyes = Commands.literal("eyes");
        eyes.executes(ctx -> {
            Minecraft mc = Minecraft.getInstance();

            GlowingEyesClient.getClientScheduler().runLater(() -> {
                mc.setScreen(new EyesEditorScreen());
            }, 1L);

            return 1;
        });

        dispatcher.register(eyes);
    }
}
