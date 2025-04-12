package me.andreasmelone.glowingeyes.forge.common.component.eyes;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.core.Direction;
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

public class GlowingEyesHandler implements INBTSerializable<CompoundTag>, ICapabilityProvider {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(GlowingEyes.MOD_ID, "glowingeyes");
    private static final Logger LOGGER = LogUtils.getLogger();

    IGlowingEyes glowingeyes = new GlowingEyesImpl();
    LazyOptional<IGlowingEyes> instance = LazyOptional.of(() -> glowingeyes);

    @Override
    public CompoundTag serializeNBT() {
        return glowingeyes.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        glowingeyes.deserializeNBT(nbt);
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
