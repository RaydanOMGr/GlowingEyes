package me.andreasmelone.glowingeyes.common.capability;

import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler {
    public static final ResourceLocation GLOWING_EYES_CAPABILITY =
            new ResourceLocation(ModInfo.MODID, "glowingEyes");

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof EntityPlayer)) return;
        event.addCapability(GLOWING_EYES_CAPABILITY, new GlowingEyesProvider());
    }
}