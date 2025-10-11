package me.andreasmelone.glowingeyes.neoforge.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.io.File;

public class GlowingEyesClientEvents {
    private final ClientModContext mod;
    private final File saveFile = new File(GlowingEyes.LOCAL_SAVE_PATH);

    public GlowingEyesClientEvents(ClientModContext mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        GlowingEyes.SCHEDULER_CLIENT.tick();

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        while (GlowingEyesKeybindings.TOGGLE_MAPPING.consumeClick()) {
            GlowingEyesComponent.setToggledOn(player, !GlowingEyesComponent.isToggledOn(player));
            ClientGlowingEyesComponent.sendUpdate();
        }
        while (GlowingEyesKeybindings.EYES_EDITOR_MAPPING.consumeClick()) {
            if (Minecraft.getInstance().screen != null) return;
            Minecraft.getInstance().setScreen(new EyesEditorScreen(this.mod));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        LocalPlayer player = event.getPlayer();
        if(Minecraft.getInstance().player == player) {
            ClientPlayerDataComponent.sendRequest();
            if(this.saveFile.isFile() && this.saveFile.exists()) {
                CompoundTag deserialized = Util.readFromFile(this.saveFile);
                if (deserialized != null) {
                    GlowingEyesComponent.load(player, player.registryAccess(), deserialized);
                } else {
                    Util.LOGGER.error("Failed to read file {}!", this.saveFile.getName());
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        LocalPlayer player = event.getPlayer();
        if(player != null && Minecraft.getInstance().player == player) {
            DynamicTextureCache.clear();
            if (!ClientPlayerDataComponent.isModOnServer()) {
                CompoundTag serialized = GlowingEyesComponent.serialize(player, player.registryAccess());
                if (!Util.writeToFile(this.saveFile, serialized)) {
                    Util.LOGGER.error("Failed to write file {}!", this.saveFile.getName());
                } else Util.LOGGER.info("Saved glowing eyes data to {}!", this.saveFile.getName());
            }
            ClientPlayerDataComponent.setIsModOnServer(false);
        }
    }
}
