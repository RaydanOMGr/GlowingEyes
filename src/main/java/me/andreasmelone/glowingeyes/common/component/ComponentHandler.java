package me.andreasmelone.glowingeyes.common.component.eyes;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.resources.ResourceLocation;

public class GlowingEyesHandler implements EntityComponentInitializer {
    public static final ComponentKey<IGlowingEyes> GLOWING_EYES =
            ComponentRegistry.getOrCreate(new ResourceLocation(GlowingEyes.MOD_ID, "glowingeyes"), IGlowingEyes.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(GLOWING_EYES, player -> new GlowingEyesImpl(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
