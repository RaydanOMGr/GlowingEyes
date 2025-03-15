package me.andreasmelone.glowingeyes.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.util.color.ColorType;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.function.Function;

public class GlowingEyesInfoCommand<T extends SharedSuggestionProvider> extends AbstractClientCommand<T> {
    public GlowingEyesInfoCommand(Function<String, LiteralArgumentBuilder<T>> stringLiteralArgumentBuilderFunction) {
        super(stringLiteralArgumentBuilderFunction);
    }

    public void register(ClientModContext mod, CommandDispatcher<T> dispatcher) {
        LiteralArgumentBuilder<T> command = createArgumentBuilder("glowingeyesinfo");
        command.executes(context -> {
            LocalPlayer player = Minecraft.getInstance().player;

            Color selectedColor = mod.getModVariables().getFinalColor();
            float brightness = mod.getModVariables().getBrightness();
            Color finalColor = mod.getModVariables().getFinalColor();
            boolean toggledOn = GlowingEyesComponent.isToggledOn(player);

            player.sendSystemMessage(Component.literal("Selected color: ")
                    .append(Component.literal(ColorType.HEX.get(selectedColor)))
                    .withStyle(Style.EMPTY.withColor(selectedColor.getRGB())));
            player.sendSystemMessage(Component.literal("Brightness: ")
                    .append(String.format("%.2f", brightness)));
            player.sendSystemMessage(Component.literal("Final color: ")
                    .append(Component.literal(ColorType.HEX.get(finalColor)))
                    .withStyle(Style.EMPTY.withColor(finalColor.getRGB())));
            player.sendSystemMessage(Component.literal("Toggled ")
                    .append(toggledOn ? "ON" : "OFF"));
            player.sendSystemMessage(Component.literal("Layout: ")
                    .append(GlowingEyesComponent.getGlowingEyesMap(player).toString()));


            return 1;
        });
        dispatcher.register(command);
    }
}
