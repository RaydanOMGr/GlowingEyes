package me.andreasmelone.glowingeyes.fabric.common.component;

import com.mojang.logging.LogUtils;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.fabric.common.component.eyes.IGlowingEyes;
import me.andreasmelone.glowingeyes.fabric.common.component.data.IPlayerData;
import me.andreasmelone.glowingeyes.fabric.common.component.data.PlayerDataImpl;
import me.andreasmelone.glowingeyes.fabric.common.component.eyes.GlowingEyesImpl;

public class ComponentHandler implements EntityComponentInitializer {
    public static final ComponentKey<IGlowingEyes> GLOWING_EYES =
            ComponentRegistry.getOrCreate(GlowingEyesComponent.IDENTIFIER, IGlowingEyes.class);
    public static final ComponentKey<IPlayerData> PLAYER_DATA =
            ComponentRegistry.getOrCreate(PlayerDataComponent.IDENTIFIER, IPlayerData.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        LogUtils.getLogger().info("Registering Glowing Eyes component");
        registry.registerForPlayers(GLOWING_EYES, player -> new GlowingEyesImpl(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(PLAYER_DATA, player -> new PlayerDataImpl(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
