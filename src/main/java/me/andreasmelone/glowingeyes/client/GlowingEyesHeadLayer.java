package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import me.andreasmelone.glowingeyes.common.util.RenderUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GlowingEyesHeadLayer implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;

    private final ResourceLocation[] eyeOverlays;

    public GlowingEyesHeadLayer(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
        eyeOverlays = new ResourceLocation[ModInfo.EYE_TYPE_COUNT];
        for (int i = 0; i < eyeOverlays.length; i++) {
            eyeOverlays[i] = new ResourceLocation(ModInfo.MODID + ":textures/entity/eyes/eyes" + (i) + ".png");
        }
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing,
                              float limbSwingAmount, float partialTicks, float ageInTicks,
                              float netHeadYaw, float headPitch, float scale) {
        IGlowingEyesCapability glowingEyes = player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP);

        boolean serverHasMod = GlowingEyes.serverHasMod;
        boolean hasGlowingEyes;
        int glowingEyesType;
        if(serverHasMod) {
            hasGlowingEyes = glowingEyes.hasGlowingEyes();
            glowingEyesType = glowingEyes.getGlowingEyesType();
        } else if(player == GlowingEyes.proxy.getPlayer()) {
            hasGlowingEyes = GlowingEyes.proxy.getDataSaveFile().getHasGlowingEyes();
            glowingEyesType = GlowingEyes.proxy.getDataSaveFile().getGlowingEyesType();
        } else {
            hasGlowingEyes = false;
            glowingEyesType = 0;
        }

        if (hasGlowingEyes) {
            int eyeType = Math.max(
                    0, Math.min(glowingEyesType, eyeOverlays.length - 1)
            );
            GlStateManager.pushMatrix();
            if (player.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.playerRenderer.getMainModel().bipedHead.render(scale);

            RenderUtil.renderGlowing(playerRenderer, playerRenderer.getMainModel().bipedHead, eyeOverlays[eyeType], 240f, player, scale);

            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return true;
    }


    private void renderNormalEyes(int eyeType, float scale) {
        this.playerRenderer.bindTexture(eyeOverlays[eyeType]);
        this.playerRenderer.getMainModel().bipedHead.render(scale);
    }
}