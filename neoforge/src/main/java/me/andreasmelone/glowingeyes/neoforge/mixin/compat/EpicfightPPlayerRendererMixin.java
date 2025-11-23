package me.andreasmelone.glowingeyes.neoforge.mixin.compat;

import me.andreasmelone.glowingeyes.client.render.GlowingEyesHeadLayer;
import me.andreasmelone.glowingeyes.neoforge.client.compat.epicfight.PatchedGlowingEyesLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@Mixin(PPlayerRenderer.class)
public abstract class EpicfightPPlayerRendererMixin extends PHumanoidRenderer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, PlayerRenderer, HumanoidMesh> {
    @Shadow
    public abstract AssetAccessor<HumanoidMesh> getMeshProvider(AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch);

    private EpicfightPPlayerRendererMixin(AssetAccessor<HumanoidMesh> mesh, EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(mesh, context, entityType);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    public void addPatchedGlowingEyesLayer(EntityRendererProvider.Context context, EntityType<?> entityType, CallbackInfo ci) {
        this.addPatchedLayer(GlowingEyesHeadLayer.class, new PatchedGlowingEyesLayer<>(Meshes.BIPED));
    }
}
