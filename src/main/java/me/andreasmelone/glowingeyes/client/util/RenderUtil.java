package me.andreasmelone.glowingeyes.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// THIS CLASS HAS BEEN PARTIALLY COPIED FROM THE VAMPIRISM MOD!!!
@SideOnly(Side.CLIENT)
public class RenderUtil {
    // Tbf I have no idea what this does, but it's needed for the glowing eyes to work so I'll just leave it here

    /**
     * Render the given model part using the given texture with a glowing lightmap (like vanilla spider)
     *
     * @param brightness Between 0 and 255f
     */
    public static <T extends EntityLivingBase> void renderGlowing(RenderLivingBase<T> render, ModelRenderer modelPart, ResourceLocation texture, float brightness, T entity, float scale) {
        render.bindTexture(texture);

        startGlowing(entity.isInvisible(), brightness);
        modelPart.render(scale);
        endGlowing(entity.getBrightnessForRender());

    }

    private static void startGlowing(boolean entityInvisible, float brightness){
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        GlStateManager.depthMask(!entityInvisible);


        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
    }

    private static void endGlowing(int brightnessForRender){
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        int j = brightnessForRender % 65536;
        int k = brightnessForRender / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        GlStateManager.disableBlend();
    }
}