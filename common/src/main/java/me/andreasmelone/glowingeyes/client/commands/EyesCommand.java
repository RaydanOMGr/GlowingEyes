package me.andreasmelone.glowingeyes.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.function.Function;

public class EyesCommand<T extends SharedSuggestionProvider> extends AbstractClientCommand<T> {
    public EyesCommand(Function<String, LiteralArgumentBuilder<T>> stringLiteralArgumentBuilderFunction) {
        super(stringLiteralArgumentBuilderFunction);
    }

    @Override
    public void register(ClientModContext mod, CommandDispatcher<T> dispatcher) {
        LiteralArgumentBuilder<T> command = createArgumentBuilder("eyes");
        command.executes(ctx -> {
            Minecraft mc = Minecraft.getInstance();

            GlowingEyes.SCHEDULER_CLIENT.runLater(() -> {
                mc.setScreen(new EyesEditorScreen(mod));
            }, 1L);

            return 1;
        });

        dispatcher.register(command);
    }
}
