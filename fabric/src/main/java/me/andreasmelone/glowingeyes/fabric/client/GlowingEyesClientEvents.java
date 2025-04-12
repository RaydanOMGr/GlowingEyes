package me.andreasmelone.glowingeyes.fabric.client;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.component.data.ClientPlayerDataComponent;
import me.andreasmelone.glowingeyes.client.util.DynamicTextureCache;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;

import java.io.File;

public class GlowingEyesClientEvents {
    public static void registerEvents() {
        File saveFile = new File(FabricLoader.getInstance().getGameDir().toFile(), GlowingEyes.LOCAL_SAVE_PATH);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            GlowingEyes.SCHEDULER_CLIENT.tick();
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientPlayerDataComponent.sendRequest();
            if(saveFile.isFile() && saveFile.exists()) {
                CompoundTag deserialized = Util.readFromFile(saveFile);
                if (deserialized != null) {
                    GlowingEyesComponent.load(client.player, deserialized);
                } else {
                    Util.LOGGER.error("Failed to read file {}!", saveFile.getName());
                }
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            DynamicTextureCache.clear();
            if(!ClientPlayerDataComponent.isModOnServer()) {
                CompoundTag serialized = GlowingEyesComponent.serialize(client.player);
                if (!Util.writeToFile(saveFile, serialized)) {
                    Util.LOGGER.error("Failed to write file {}!", saveFile.getName());
                } else Util.LOGGER.info("Saved glowing eyes data to {}!", saveFile.getName());
            }
            ClientPlayerDataComponent.setIsModOnServer(false);
        });
    }
}
