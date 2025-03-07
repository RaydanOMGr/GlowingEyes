package me.andreasmelone.glowingeyes.neoforge.client.render;

import me.andreasmelone.glowingeyes.client.render.GlowingEyesHeadLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class RenderManager {
    static <T extends Player, Q extends EntityModel<O>,
            Z extends HumanoidModel<O>, I extends LivingEntity, O extends HumanoidRenderState,
            S extends PlayerRenderState, M extends EntityModel<? super S>>
    void _onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model s : event.getSkins()) {
            LivingEntityRenderer<T, O, Q> renderPlayer = event.getSkin(s);
            if (renderPlayer != null && renderPlayer.getModel() instanceof HumanoidModel) {
                LivingEntityRenderer<T, S, M> renderPlayer2 = (LivingEntityRenderer<T, S, M>) renderPlayer;
                renderPlayer2.addLayer(new GlowingEyesHeadLayer<>(renderPlayer2));
            }
        }
    }

    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        _onAddLayers(event);
    }
}
