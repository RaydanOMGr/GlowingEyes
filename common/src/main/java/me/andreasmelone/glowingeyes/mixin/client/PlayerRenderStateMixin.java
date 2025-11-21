package me.andreasmelone.glowingeyes.mixin.client;

import me.andreasmelone.glowingeyes.client.render.IGlowingEyesRenderState;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(AvatarRenderState.class)
public class PlayerRenderStateMixin implements IGlowingEyesRenderState {
    @Unique
    private Map<Point, Color> glowingEyes$glowingEyesMap;
    @Unique
    private boolean glowingEyes$toggledOn;

    @Override
    public Map<Point, Color> glowingEyes$getGlowingEyesMap() {
        return this.glowingEyes$glowingEyesMap;
    }

    @Override
    public void glowingEyes$setGlowingEyesMap(Map<Point, Color> glowingEyesMap) {
        this.glowingEyes$glowingEyesMap = glowingEyesMap;
    }

    @Override
    public boolean glowingEyes$isToggledOn() {
        return this.glowingEyes$toggledOn;
    }

    @Override
    public void glowingEyes$setToggledOn(boolean toggledOn) {
        this.glowingEyes$toggledOn = toggledOn;
    }
}
