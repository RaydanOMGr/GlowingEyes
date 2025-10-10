package me.andreasmelone.glowingeyes.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;

import java.util.function.Consumer;

public class ShaderManager {
    public static final ShaderProgram COLOR_SQUARE_SHADER = new ShaderProgram(
            Util.id(GlowingEyes.MOD_ID, "core/color_square"), DefaultVertexFormat.POSITION_TEX_COLOR, ShaderDefines.EMPTY
    );
    public static final ShaderProgram HUE_BAR_SHADER = new ShaderProgram(
            Util.id(GlowingEyes.MOD_ID, "core/hue_bar"), DefaultVertexFormat.POSITION_TEX_COLOR, ShaderDefines.EMPTY
    );

    public static void register(Consumer<ShaderProgram> registrationCallback) {
        registrationCallback.accept(COLOR_SQUARE_SHADER);
        registrationCallback.accept(HUE_BAR_SHADER);
    }
}
