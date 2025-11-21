package me.andreasmelone.glowingeyes.mixin.client;

import com.mojang.blaze3d.textures.GpuTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GpuTexture.class)
public interface GpuTextureAccessor {
    @Accessor
    void setUsage(int usage);
}
