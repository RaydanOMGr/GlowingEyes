package me.andreasmelone.glowingeyes.client.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GLX;
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
        File dumpFile = new File("glowingeyes-dump-" + System.currentTimeMillis() + ".json");
        try (OutputStream out = new FileOutputStream(dumpFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject obj = new JsonObject();

            obj.addProperty("mod_version", LoaderUtils.MOD_VERSION);
            obj.addProperty("loader", LoaderUtils.LOADER_NAME);
            obj.addProperty("minecraft_version", SharedConstants.getCurrentVersion().getName());

            obj.addProperty("optifine_present", OptifineUtils.IS_OPTIFINE_PRESENT);
            obj.addProperty("iris_present", IrisUtils.IS_IRIS_PRESENT);

            obj.addProperty("cpu", GLX._getCpuInfo());
            obj.addProperty("gpu", GlUtil.getRenderer());

            JsonArray mods = new JsonArray();
            for (int i = 0; i < LoaderUtils.LOADED_MODS.length; i++) {
                LoaderUtils.ModData data = LoaderUtils.LOADED_MODS[i];
                JsonObject modObject = new JsonObject();
                modObject.addProperty("name", data.name());
                modObject.addProperty("id", data.id());
                modObject.addProperty("version", data.version());
                mods.add(modObject);
            }
            obj.add("loaded_mods", mods);

            obj.add("selected_color", colorToJson(selectedColor));
            obj.addProperty("brightness", brightness);
            obj.add("finalColor", colorToJson(finalColor));
            obj.addProperty("toggled_on", toggledOn);

            if (OptifineUtils.IS_OPTIFINE_PRESENT) {
                obj.addProperty("optifine_shader", OptifineUtils.getShaderName());
            }
            if (IrisUtils.IS_IRIS_PRESENT) {
                obj.addProperty("iris_shader", IrisUtils.getShaderName());
            }

            JsonArray compatPlugins = new JsonArray();
            for (CompatPlugin plugin : CompatPluginRegistry.getCompatPlugins()) {
                CompatPlugin.Info info = plugin.getPluginInfo();
                JsonObject compatPluginObj = new JsonObject();
                compatPluginObj.addProperty("id", info.id().toString());
                compatPluginObj.addProperty("name", info.name());
                compatPluginObj.addProperty("version", info.version());
                compatPlugins.add(compatPluginObj);
            }
            obj.add("compat_plugins", compatPlugins);

            JsonArray resourcePacks = new JsonArray();
            for (Pack pack : mc.getResourcePackRepository().getSelectedPacks()) {
                JsonObject resourcePack = new JsonObject();
                resourcePack.addProperty("title", pack.getTitle().getString());
                resourcePack.addProperty("description", pack.getDescription().getString());
            }
            obj.add("resourcepacks", resourcePacks);

            JsonObject skinMap = new JsonObject();
            JsonObject currentPartObj = new JsonObject();

            ISkinPart currentPart = null;
            for (Map.Entry<Point, Color> entry : map.entrySet()) {
                Point point = entry.getKey();
                Color color = entry.getValue();
                ISkinPart newPart = ISkinPart.getFromCoordinates(point.getX(), point.getY(), SkinUtil.isSlim());

                if (currentPart != newPart) {
                    if(currentPart != null) skinMap.add(currentPart.toString(), currentPartObj);
                    currentPartObj = new JsonObject();
                }

                currentPartObj.add("x: " + point.getX() + ", y: " + point.getY(), colorToJson(color));
                currentPart = newPart;
            }
            if(currentPart != null) skinMap.add(currentPart.toString(), currentPartObj);
            obj.add("glowing_map", skinMap);

            obj.addProperty("presets_version", PresetManager.DATA_VERSION);
            obj.add("presets", PresetManager.getInstance().serializePresets());

            out.write(gson.toJson(obj).getBytes(StandardCharsets.UTF_8));

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

    private static JsonObject colorToJson(Color color) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("r", color.getRed());
        jsonObject.addProperty("g", color.getGreen());
        jsonObject.addProperty("b", color.getBlue());
        return jsonObject;
    }
}
