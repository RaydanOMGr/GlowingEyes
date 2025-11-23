package me.andreasmelone.glowingeyes.neoforge.client.compat.epicfight;

import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.render.GlowingEyesHeadLayer;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.ModelRenderLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PatchedGlowingEyesLayer<E extends Player, T extends LivingEntityPatch<E>, M extends PlayerModel<E>, AM extends SkinnedMesh> extends ModelRenderLayer<E, T, M, GlowingEyesHeadLayer<E, M>, AM> {
    public PatchedGlowingEyesLayer(AssetAccessor<AM> mesh) {
        super(mesh);
    }

    @Override
    protected void renderLayer(T patch, E player, @Nullable GlowingEyesHeadLayer<E, M> glowingEyesHeadLayer, PoseStack pose, MultiBufferSource bufferSource, int i, OpenMatrix4f[] poses, float v, float v1, float v2, float v3) {
        if(GlowingEyesComponent.isToggledOn(player) && !player.isInvisible()) {
            ResourceLocation eyeOverlayResource = DynamicTextureCache.getTexture(GlowingEyesComponent.getGlowingEyesMap(player));

            RenderType eyeRenderType = RenderType.eyes(eyeOverlayResource);
            int packerOverlay = LivingEntityRenderer.getOverlayCoords(player, 0);
            this.mesh.get().draw(pose, bufferSource, eyeRenderType, i, 1.0F, 1.0F, 1.0F, 1.0F, packerOverlay, patch.getArmature(), poses);
        }
    }
}
