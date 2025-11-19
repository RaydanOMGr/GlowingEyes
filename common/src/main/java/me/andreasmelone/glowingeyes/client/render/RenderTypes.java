package me.andreasmelone.glowingeyes.client.render;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

import java.util.function.Function;

public class RenderTypes extends RenderStateShard {
    private static final Function<ResourceLocation, RenderType> COLOR_SQUARE = Util.memoize((resourceLocation) ->
            RenderType.create("color_square",
                    RenderType.TRANSIENT_BUFFER_SIZE,
                    PipelineManager.COLOR_SQUARE_SHADER,
                    RenderType.CompositeState.builder()
                            .setTextureState(new TextureStateShard(resourceLocation, TriState.DEFAULT, false))
                            .createCompositeState(false)
            )
    );

    private static final Function<ResourceLocation, RenderType> HUE_BAR = Util.memoize((resourceLocation) ->
            RenderType.create("hue_bar",
                    RenderType.TRANSIENT_BUFFER_SIZE,
                    PipelineManager.HUE_BAR_SHADER,
                    RenderType.CompositeState.builder()
                            .setTextureState(new TextureStateShard(resourceLocation, TriState.DEFAULT, false))
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
