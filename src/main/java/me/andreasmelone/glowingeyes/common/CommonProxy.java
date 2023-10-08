package me.andreasmelone.glowingeyes.common;

import me.andreasmelone.glowingeyes.client.modless.DataSaveFile;
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

public abstract class CommonProxy {
    private DataSaveFile dataSaveFile;

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

    public abstract EntityPlayer getPlayer();

    public DataSaveFile getDataSaveFile() {
        return dataSaveFile;
    }
}
