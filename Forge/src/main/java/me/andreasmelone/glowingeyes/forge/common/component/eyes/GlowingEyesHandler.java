package me.andreasmelone.glowingeyes.forge.common.component.eyes;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;

public class GlowingEyesHandler implements INBTSerializable<CompoundTag>, ICapabilityProvider {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(GlowingEyes.MOD_ID, "glowingeyes");
    private static final Logger LOGGER = LogUtils.getLogger();

    IGlowingEyes glowingeyes = new GlowingEyesImpl();
    LazyOptional<IGlowingEyes> instance = LazyOptional.of(() -> glowingeyes);

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("toggledOn", glowingeyes.isToggledOn());
        tag.put("glowingEyesMap", Util.toCompoundTag(Point.CODEC_STRING, Color.CODEC, glowingeyes.getGlowingEyesMap()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        glowingeyes.setToggledOn(compoundTag.getBoolean("toggledOn"));
        if (compoundTag.get("glowingEyesMap") instanceof ByteArrayTag) {
            glowingeyes.setGlowingEyesMap(new HashMap<>());
            LOGGER.warn("Detected glowing eyes map of old format!");
            LOGGER.warn("Your current eyes will be erased.");
            return;
        }
        glowingeyes.setGlowingEyesMap(Util.toMap(Point.CODEC_STRING, Color.CODEC, compoundTag.getCompound("glowingEyesMap")));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        return GlowingEyesComponentImpl.INSTANCE.orEmpty(capability, instance);
    }

    @SubscribeEvent
    public static void attach(final AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {
            GlowingEyesHandler glowingEyesHandler = new GlowingEyesHandler();
            event.addCapability(GlowingEyesHandler.IDENTIFIER, glowingEyesHandler);
        }
    }
}
