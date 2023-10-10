package me.andreasmelone.glowingeyes.common;

import me.andreasmelone.glowingeyes.client.data.DataSaveFile;
import me.andreasmelone.glowingeyes.common.capability.CapabilityHandler;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesStorage;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.NetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CommonProxy {
    private DataSaveFile dataSaveFile;
    private HashMap<EntityPlayer, List<UUID>> playersTracking = new HashMap<>();

    public void preInit(FMLPreInitializationEvent e) {
    }

    public void init(FMLInitializationEvent e) {
        CapabilityManager.INSTANCE.register(IGlowingEyesCapability.class, new GlowingEyesStorage(), GlowingEyesCapability::new);
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());

        dataSaveFile = null;
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

    public DataSaveFile getDataSaveFile() {
        return dataSaveFile;
    }
    public HashMap<EntityPlayer, List<UUID>> getPlayersTracking() {
        return playersTracking;
    }
    public void setPlayersTracking(HashMap<EntityPlayer, List<UUID>> playersTracking) {
        this.playersTracking = playersTracking;
    }
}