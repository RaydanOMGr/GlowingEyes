package me.andreasmelone.glowingeyes.common.capability.data;

import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDataHandler implements ICapabilityProvider {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(GlowingEyes.MOD_ID, "playerdata");

    IPlayerData playerData = new PlayerDataImpl();
    LazyOptional<IPlayerData> instance = LazyOptional.of(() -> playerData);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        return PlayerDataCapability.INSTANCE.orEmpty(capability, instance);
    }

    @SubscribeEvent
    public static void attach(final AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {
            PlayerDataHandler glowingEyesHandler = new PlayerDataHandler();
            event.addCapability(PlayerDataHandler.IDENTIFIER, glowingEyesHandler);
        }
    }
}
