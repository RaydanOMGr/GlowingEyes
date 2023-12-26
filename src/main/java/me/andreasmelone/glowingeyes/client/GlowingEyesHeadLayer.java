package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.RenderUtil;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesProvider;
import me.andreasmelone.glowingeyes.common.capability.eyes.IGlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

// THIS CLASS HAS BEEN PARTIALLY COPIED FROM THE VAMPIRISM MOD!!!
@SideOnly(Side.CLIENT)
public class GlowingEyesHeadLayer implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;

    public GlowingEyesHeadLayer(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing,
                              float limbSwingAmount, float partialTicks, float ageInTicks,
                              float netHeadYaw, float headPitch, float scale) {
        IGlowingEyesCapability glowingEyes = player.getCapability(GlowingEyesProvider.CAPABILITY, null);
        boolean serverHasMod = GlowingEyes.serverHasMod;
        boolean isToggled = true;

        if(player.isInvisible()) return;

        HashMap<Point, Color> pixelMap;
        if (serverHasMod) {
            pixelMap = glowingEyes.getGlowingEyesMap();
            isToggled = glowingEyes.isToggledOn();
        } else if(player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID())) { // if the player is the local player
            pixelMap = GlowingEyes.proxy.getPixelMap();
            isToggled = GlowingEyes.proxy.isToggledOn();
        } else {
            pixelMap = new HashMap<>();
            isToggled = false;
        }

        if(!isToggled) return;

        // Push the matrix
        // This is needed to make sure the texture is rendered at the right position
        GlStateManager.pushMatrix();
        // Check if the player is sneaking
        if (player.isSneaking()) {
            // In that case lower the texture a bit
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }

        // Create a new texture
        BufferedImage eyeOverlayTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        // Loop through the pixel map, which is defined in the proxy
        for (Point point : pixelMap.keySet()) {
            // Get the color of the pixel
            Color color = pixelMap.get(point);
            // Set the color of the pixel in the previously defined texture
            eyeOverlayTexture.setRGB(point.x + (64 / 8), point.y + (64 / 8), color.getRGB());
        }

        // Create a dynamic texture so we can use it in the render method
        DynamicTexture eyeOverlay = new DynamicTexture(eyeOverlayTexture);
        // Get the resource location of the texture
        ResourceLocation eyeOverlayResource = playerRenderer.getRenderManager().renderEngine
                .getDynamicTextureLocation(ModInfo.MODID, eyeOverlay);

        // Then render it
        RenderUtil.renderGlowing(playerRenderer, playerRenderer.getMainModel().bipedHead, eyeOverlayResource, 240f, player, scale);

        // Now, since we are done, pop the matrix we have pushed before so it doesn't cause any issues
        GlStateManager.popMatrix();
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}
