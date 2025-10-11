package me.andreasmelone.glowingeyes.client.commands;

import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.compat.CompatPlugin;
import me.andreasmelone.glowingeyes.client.compat.CompatPluginRegistry;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.client.gui.skin.ISkinPart;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.util.IrisUtils;
import me.andreasmelone.glowingeyes.client.util.OptifineUtils;
import me.andreasmelone.glowingeyes.client.util.SkinUtil;
import me.andreasmelone.glowingeyes.client.util.color.ColorType;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.LoaderUtils;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.repository.Pack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

public class EyesCommand<T extends SharedSuggestionProvider> extends AbstractClientCommand<T> {
    public EyesCommand(Function<String, LiteralArgumentBuilder<T>> stringLiteralArgumentBuilderFunction) {
        super(stringLiteralArgumentBuilderFunction);
    }

    @Override
    public void register(ClientModContext mod, CommandDispatcher<T> dispatcher) {
        LiteralArgumentBuilder<T> command = this.literal("eyes");
        command.executes(ctx -> this.open(mod))
                .then(this.literal("open").executes(ctx -> this.open(mod)))
                .then(this.literal("dump").executes(ctx -> this.dump(mod)))
                .then(this.literal("debuginfo").executes(ctx -> this.debugInfo(mod)));

        dispatcher.register(command);
    }

    private int open(ClientModContext mod) {
        Minecraft mc = Minecraft.getInstance();

        GlowingEyes.SCHEDULER_CLIENT.runLater(() -> {
            mc.setScreen(new EyesEditorScreen(mod));
        }, 1L);

        return 1;
    }

    private int dump(ClientModContext mod) {
        Minecraft mc = Minecraft.getInstance();
        Map<Point, Color> map = GlowingEyesComponent.getGlowingEyesMap(mc.player);
        Color selectedColor = mod.getModVariables().getFinalColor();
        float brightness = mod.getModVariables().getBrightness();
        Color finalColor = mod.getModVariables().getFinalColor();
        boolean toggledOn = GlowingEyesComponent.isToggledOn(mc.player);
        File dumpFile = new File("glowingeyes-dump-" + System.currentTimeMillis() + ".txt");
        try (OutputStream out = new FileOutputStream(dumpFile)) {
            StringBuilder sb = new StringBuilder();
            sb.append('V').append(LoaderUtils.MOD_VERSION)
                    .append(" L").append(LoaderUtils.LOADER_NAME)
                    .append(" MC").append(SharedConstants.getCurrentVersion().getName())
                    .append("\n");
            sb.append("modOptifine=").append(OptifineUtils.IS_OPTIFINE_PRESENT)
                    .append(" modIris=").append(IrisUtils.IS_IRIS_PRESENT).append("\n\n");

            sb.append("CPU: ").append(GlUtil.getCpuInfo()).append('\n')
                    .append("GPU: ").append(GlUtil.getRenderer()).append("\n\n");

            sb.append("startmods\n");
            for (int i = 0; i < LoaderUtils.LOADED_MODS.length; i++) {
                LoaderUtils.ModData data = LoaderUtils.LOADED_MODS[i];
                sb.append("\tname \"").append(data.name()).append("\" id ").append(data.id()).append(" version ").append(data.version()).append('\n');
            }
            sb.append("endmods\n\n");

            sb.append("selectedColor: ")
                    .append("{ r: ").append(selectedColor.getRed()).append(", g: ").append(selectedColor.getGreen()).append(", b: ").append(selectedColor.getBlue()).append(" }\n");
            sb.append("brightness: ").append(brightness).append("f\n");
            sb.append("finalColor: ")
                    .append("{ r: ").append(finalColor.getRed()).append(", g: ").append(finalColor.getGreen()).append(", b: ").append(finalColor.getBlue()).append(" }\n");
            sb.append("toggledOn: ").append(toggledOn).append("\n\n");

            if (OptifineUtils.IS_OPTIFINE_PRESENT) {
                sb.append("ofshader: ").append(OptifineUtils.getShaderName()).append("\n\n");
            }
            if (IrisUtils.IS_IRIS_PRESENT) {
                sb.append("irisshader: ").append(IrisUtils.getShaderName()).append("\n\n");
            }

            sb.append("startcompatplugins\n");
            for (CompatPlugin plugin : CompatPluginRegistry.getCompatPlugins()) {
                CompatPlugin.Info info = plugin.getPluginInfo();
                sb.append("\tid ").append(info.id().toString()).append(" name \"").append(info.name()).append("\" version ").append(info.version()).append("\n");
            }
            sb.append("endcompatplugins\n\n");

            sb.append("startresourcepacks\n");
            for (Pack pack : mc.getResourcePackRepository().getSelectedPacks()) {
                sb.append("\tid \"").append(pack.getTitle().getString())
                        .append("\" desc \"").append(pack.getDescription().getString()).append("\"\n");
            }
            sb.append("endresourcepacks\n\n");

            sb.append("startmap\nL").append(map.size());

            ISkinPart currentPart = null;
            for (Map.Entry<Point, Color> entry : map.entrySet()) {
                Point point = entry.getKey();
                Color color = entry.getValue();
                ISkinPart newPart = ISkinPart.getFromCoordinates(point.getX(), point.getY(), SkinUtil.isSlim());

                if (currentPart != newPart) {
                    sb.append("\n\t").append(newPart).append('\n');
                }

                sb.append("\tx: ").append(point.getX()).append(", y: ").append(point.getY())
                        .append(" = ")
                        .append("{ r: ").append(color.getRed()).append(", g: ").append(color.getGreen()).append(", b: ").append(color.getBlue()).append(" }")
                        .append('\n');

                currentPart = newPart;
            }
            sb.append("endmap\n\n");

            sb.append("presets version: ").append(PresetManager.DATA_VERSION).append("\n");
            sb.append("presets: ").append(PresetManager.getInstance().serializePresets());

            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));

            Component clickableComponent = Component.literal(dumpFile.getName())
                    .withStyle(ChatFormatting.UNDERLINE)
                    .withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, dumpFile.getAbsolutePath())));
            mc.player.displayClientMessage(Component.literal("Successfully dumped eyes to ").append(clickableComponent), false);
        } catch (IOException e) {
            LogUtils.getLogger().error("Unable to dump", e);
            mc.player.displayClientMessage(
                    Component.literal("Failed to dump! Try again or report to the mod author.").withStyle(ChatFormatting.RED), false);
        }

        return 1;
    }

    private int debugInfo(ClientModContext mod) {
        LocalPlayer player = Minecraft.getInstance().player;

        Color selectedColor = mod.getModVariables().getFinalColor();
        float brightness = mod.getModVariables().getBrightness();
        Color finalColor = mod.getModVariables().getFinalColor();
        boolean toggledOn = GlowingEyesComponent.isToggledOn(player);

        player.displayClientMessage(Component.literal("Selected color: ")
                .append(Component.literal(ColorType.HEX.get(selectedColor)))
                .withStyle(Style.EMPTY.withColor(selectedColor.getRGB())), false);
        player.displayClientMessage(Component.literal("Brightness: ")
                .append(String.format("%.2f", brightness)), false);
        player.displayClientMessage(Component.literal("Final color: ")
                .append(Component.literal(ColorType.HEX.get(finalColor)))
                .withStyle(Style.EMPTY.withColor(finalColor.getRGB())), false);
        player.displayClientMessage(Component.literal("Toggled ")
                .append(toggledOn ? "ON" : "OFF"), false);
        player.displayClientMessage(Component.literal("Layout: ")
                .append(GlowingEyesComponent.getGlowingEyesMap(player).toString()), false);

        return 1;
    }
}
