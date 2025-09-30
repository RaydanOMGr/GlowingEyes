package me.andreasmelone.glowingeyes.mixin.client;

import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(
            method = "reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("RETURN")
    )
    public void onReloadResourcePacks(boolean error, Minecraft.GameLoadCookie gameLoadCookie, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().thenApply((unused) -> {
            DynamicTextureCache.clear();
            return unused;
        });
    }
}
