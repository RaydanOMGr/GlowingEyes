package me.andreasmelone.glowingeyes.server.component;

import com.mojang.logging.LogUtils;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.server.component.data.IPlayerData;
import me.andreasmelone.glowingeyes.server.component.data.PlayerDataImpl;
import me.andreasmelone.glowingeyes.server.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.server.component.eyes.IGlowingEyes;
import net.minecraft.resources.ResourceLocation;

public class ComponentHandler implements EntityComponentInitializer {
    public static final ComponentKey<IGlowingEyes> GLOWING_EYES =
            ComponentRegistry.getOrCreate(new ResourceLocation(GlowingEyes.MOD_ID, "glowingeyes"), IGlowingEyes.class);
    public static final ComponentKey<IPlayerData> PLAYER_DATA =
            ComponentRegistry.getOrCreate(new ResourceLocation(GlowingEyes.MOD_ID, "playerdata"), IPlayerData.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        LogUtils.getLogger().info("Registering Glowing Eyes component");
        registry.registerForPlayers(GLOWING_EYES, player -> new GlowingEyesImpl(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(PLAYER_DATA, player -> new PlayerDataImpl(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
