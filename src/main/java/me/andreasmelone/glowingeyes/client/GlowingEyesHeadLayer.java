package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.RenderUtil;
import me.andreasmelone.glowingeyes.common.capability.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

// THIS CLASS HAS BEEN PARTIALLY COPIED FROM THE VAMPIRISM MOD!!!
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

        GlStateManager.pushMatrix();
        if (player.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }



        BufferedImage eyeOverlayTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        HashMap<Point, Color> pixelMap = GlowingEyes.proxy.getPixelMap();
        for (Point point : pixelMap.keySet()) {
            Color color = pixelMap.get(point);
            eyeOverlayTexture.setRGB(point.x + (64 / 8), point.y + (64 / 8), color.getRGB());
        }

        DynamicTexture eyeOverlay = new DynamicTexture(eyeOverlayTexture);
        ResourceLocation eyeOverlayResource = playerRenderer.getRenderManager().renderEngine
                .getDynamicTextureLocation(ModInfo.MODID, eyeOverlay);

        // here something goes wrong; I'd guess
        //this.playerRenderer.getMainModel().bipedHead.render(scale);
        // commenting this out just works for some reason, well, if it works, it works
        // okay, I know what it was. Vampirism has the fang rendering in this class too
        // it had a texture bind before which I removed, but I didn't remove the render part causing an issue
        
        RenderUtil.renderGlowing(playerRenderer, playerRenderer.getMainModel().bipedHead, eyeOverlayResource, 240f, player, scale);

        GlStateManager.popMatrix();
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}
