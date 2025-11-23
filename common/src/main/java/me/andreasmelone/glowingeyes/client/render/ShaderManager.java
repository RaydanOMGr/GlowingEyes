package me.andreasmelone.glowingeyes.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ShaderManager {
    protected static ShaderInstance COLOR_SQUARE_SHADER;
    protected static ShaderInstance HUE_BAR_SHADER;

    public static void register(RegistrationContext registrationCallback) {
        registrationCallback.register(Util.id(GlowingEyes.MOD_ID, "color_square"), DefaultVertexFormat.POSITION_TEX_COLOR, shader -> COLOR_SQUARE_SHADER = shader);
        registrationCallback.register(Util.id(GlowingEyes.MOD_ID, "hue_bar"), DefaultVertexFormat.POSITION_TEX_COLOR, shader -> HUE_BAR_SHADER = shader);
    }

    public static ShaderInstance colorSquare() {
        return COLOR_SQUARE_SHADER;
    }

    public static ShaderInstance hueBar() {
        return HUE_BAR_SHADER;
    }

    public interface RegistrationContext {
        void register(ResourceLocation rl, VertexFormat format, Consumer<ShaderInstance> shaderGenerator);
    }
}
