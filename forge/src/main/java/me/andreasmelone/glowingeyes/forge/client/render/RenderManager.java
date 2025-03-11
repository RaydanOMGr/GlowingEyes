package me.andreasmelone.glowingeyes.forge.client.render;

import me.andreasmelone.glowingeyes.client.render.GlowingEyesHeadLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class RenderManager {
    static <T extends Player, Q extends EntityModel<T>,
            Z extends HumanoidModel<T>, I extends LivingEntity, U extends EntityModel<I>>
    void _onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (String s : event.getSkins()) {
            LivingEntityRenderer<T, Q> renderPlayer = event.getSkin(s);
            if (renderPlayer != null && renderPlayer.getModel() instanceof HumanoidModel) {
                LivingEntityRenderer<T, Z> renderPlayer2 = (LivingEntityRenderer<T, Z>) renderPlayer;
                renderPlayer2.addLayer(new GlowingEyesHeadLayer<>(renderPlayer2));
            }
        }
    }

    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        _onAddLayers(event);
    }
}
