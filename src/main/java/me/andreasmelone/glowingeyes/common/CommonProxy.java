package me.andreasmelone.glowingeyes.common;

import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.common.capability.CapabilityHandler;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesStorage;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import me.andreasmelone.glowingeyes.common.scheduler.Scheduler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CommonProxy {
    private Scheduler scheduler;
    private HashMap<EntityPlayer, List<UUID>> playersTracking = new HashMap<>();

    public void preInit(FMLPreInitializationEvent e) {
        scheduler = new Scheduler();
    }

    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new CommonGlowingEyesEvents());

        CapabilityManager.INSTANCE.register(IGlowingEyesCapability.class, new GlowingEyesStorage(), GlowingEyesCapability::new);
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());

        NetworkHandler.init();
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    public EntityPlayer getPlayer() {
        return null;
    }

    public boolean isClient() {
        return false;
    }

    public HashMap<EntityPlayer, List<UUID>> getPlayersTracking() {
        return playersTracking;
    }

    public void setPlayersTracking(HashMap<EntityPlayer, List<UUID>> playersTracking) {
        this.playersTracking = playersTracking;
    }

    public void openGui(GuiScreen gui) {
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
    public PresetManager getPresetManager() {
        return null;
    }

    public HashMap<Point, Color> getPixelMap() {
        return null;
    }
    public void setPixelMap(HashMap<Point, Color> pixelMap) {
    }
    public Color getPixelColor() {
        return null;
    }
    public void setPixelColor(Color color) {
    }
}
