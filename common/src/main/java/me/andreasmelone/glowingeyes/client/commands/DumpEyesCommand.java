package me.andreasmelone.glowingeyes.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.gui.skin.SkinPart;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

public class DumpEyesCommand<T extends SharedSuggestionProvider> extends AbstractClientCommand<T> {
    public DumpEyesCommand(Function<String, LiteralArgumentBuilder<T>> stringLiteralArgumentBuilderFunction) {
        super(stringLiteralArgumentBuilderFunction);
    }

    @Override
    public void register(ClientModContext mod, CommandDispatcher<T> dispatcher) {
        LiteralArgumentBuilder<T> command = createArgumentBuilder("dumpeyes");
        command.executes(ctx -> {
            Minecraft mc = Minecraft.getInstance();
            Map<Point, Color> map = GlowingEyesComponent.getGlowingEyesMap(mc.player);
            File dumpFile = new File("glowingeyes-dump-" + System.currentTimeMillis() + ".txt");
            try(OutputStream out = new FileOutputStream(dumpFile)) {
                out.write('L');
                out.write(("" + map.size()).getBytes(StandardCharsets.UTF_8));
                out.write('\n');

                SkinPart currentPart = null;
                for (Map.Entry<Point, Color> entry : map.entrySet()) {
                    Point point = entry.getKey();
                    Color color = entry.getValue();
                    SkinPart newPart = SkinPart.getFromCoordinates(point.getX(), point.getY());

                    if(currentPart != newPart) {
                        out.write('\n');
                        out.write(newPart.toString().getBytes(StandardCharsets.UTF_8));
                        out.write('\n');
                    }

                    String pointString = "x: " + point.getX() + ", y: " + point.getY();
                    String colorString = "{ r: " + color.getRed() + ", g: " + color.getGreen() + ", b: " + color.getBlue() + " }";

                    out.write(pointString.getBytes(StandardCharsets.UTF_8));
                    out.write(" = ".getBytes(StandardCharsets.UTF_8));
                    out.write(colorString.getBytes(StandardCharsets.UTF_8));
                    out.write('\n');

                    currentPart = newPart;
                }

                Component clickableComponent = Component.literal(dumpFile.getName())
                        .withStyle(ChatFormatting.UNDERLINE)
                        .withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, dumpFile.getAbsolutePath())));
                mc.player.sendSystemMessage(Component.literal("Successfully dumped eyes to ").append(clickableComponent));
            } catch (IOException e) {
                LogUtils.getLogger().error("Unable to dump", e);
                mc.player.sendSystemMessage(
                        Component.literal("Failed to dump! Try again or report to the mod author.").withStyle(ChatFormatting.RED));
            }

            return 1;
        });

        dispatcher.register(command);
    }
}