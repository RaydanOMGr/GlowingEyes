package me.andreasmelone.glowingeyes.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class RenderTypes extends RenderStateShard {
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_COLOR_SQUARE = new RenderStateShard.ShaderStateShard(ShaderManager.COLOR_SQUARE_SHADER);
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_HUE_BAR = new RenderStateShard.ShaderStateShard(ShaderManager.HUE_BAR_SHADER);

    private static final Function<ResourceLocation, RenderType> COLOR_SQUARE = Util.memoize(
            resourceLocation -> RenderType.CompositeRenderType.create("glowingeyes_color_square",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            RenderType.SMALL_BUFFER_SIZE,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_COLOR_SQUARE)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false)
            )
    );

    private static final Function<ResourceLocation, RenderType> HUE_BAR = Util.memoize(
            resourceLocation -> RenderType.CompositeRenderType.create("glowingeyes_hue:bar",
                    DefaultVertexFormat.POSITION_TEX_COLOR,
                    VertexFormat.Mode.QUADS,
                    RenderType.SMALL_BUFFER_SIZE,
                    RenderType.CompositeState.builder()
                            .setShaderState(RENDERTYPE_HUE_BAR)
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                            .createCompositeState(false)
            )
    );

    public static RenderType colorSquare(ResourceLocation rl) {
        return COLOR_SQUARE.apply(rl);
    }

    public static RenderType hueBar(ResourceLocation rl) {
        return HUE_BAR.apply(rl);
    }

    private RenderTypes(String name, Runnable setupState, Runnable clearState) {
        super(name, setupState, clearState);
        throw new RuntimeException("This constructor should not be called, this class only extends RenderStateShard in order to access its fields");
        // view the exception above as a sort of documentation
    }
}
