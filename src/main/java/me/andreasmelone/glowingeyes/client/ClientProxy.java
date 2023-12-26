package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.client.commands.EyesCommand;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.common.CommonProxy;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.HashMap;

public class ClientProxy extends CommonProxy {
    public static KeyBinding toggleKeyBinding = new KeyBinding("key.glowingeyes.toggle", Keyboard.KEY_G, "key.categories.glowingeyes");
    public static KeyBinding eyesEditorKeyBinding = new KeyBinding("key.glowingeyes.openeditor", Keyboard.KEY_H, "key.categories.glowingeyes");

    public static KeyBinding[] keyBindings = new KeyBinding[] { toggleKeyBinding, eyesEditorKeyBinding };

    PresetManager presetManager = new PresetManager();

    HashMap<Point, Color> pixelMap = new HashMap<>();
    boolean isToggledOn = false;

    Color currentColor = ModInfo.DEFAULT_EYE_COLOR;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        e.getModLog().info("Registering commands");
        ClientCommandHandler.instance.registerCommand(new EyesCommand());
        ClientCommandHandler.instance.getCommands().forEach((s, command) -> e.getModLog().info("Registered command: " + s));

        presetManager.loadPresets();
        e.getModLog().info("Registered commands");
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new ClientGlowingEyesEvents());

        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        for (RenderPlayer renderPlayer : manager.getSkinMap().values()) {
            renderPlayer.addLayer(new GlowingEyesHeadLayer(renderPlayer));
        }
    }

    @Override
    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }
    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public void openGui(GuiScreen gui) {
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    @Override
    public HashMap<Point, Color> getPixelMap() {
        return pixelMap;
    }

    @Override
    public void setPixelMap(HashMap<Point, Color> pixelMap) {
        this.pixelMap = pixelMap;
    }

    @Override
    public Color getPixelColor() {
        return currentColor;
    }

    @Override
    public void setPixelColor(Color color) {
        this.currentColor = color;
    }

    @Override
    public boolean isToggledOn() {
        return isToggledOn;
    }

    @Override
    public void setToggledOn(boolean toggledOn) {
        isToggledOn = toggledOn;
    }
}
