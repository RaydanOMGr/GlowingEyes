package me.andreasmelone.glowingeyes.fabric.client.render;

import me.andreasmelone.glowingeyes.client.render.GlowingEyesHeadLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class RenderManager {
    @SuppressWarnings("unchecked")
    public static void init() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if(entityType == EntityType.PLAYER) {
                registrationHelper.register(
                        new GlowingEyesHeadLayer<>((LivingEntityRenderer<? extends Player, PlayerRenderState, EntityModel<PlayerRenderState>>) entityRenderer)
                );
            }
        });
    }
}
