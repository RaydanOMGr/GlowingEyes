package me.andreasmelone.glowingeyes.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class RenderManager {
    public static void init() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if(entityType == EntityType.PLAYER) {
                registrationHelper.register(new GlowingEyesHeadLayer<>((RenderLayerParent<Player, PlayerModel<Player>>) entityRenderer));
            }
        });
    }
}
