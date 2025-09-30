package me.andreasmelone.glowingeyes.client.compat;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public abstract class CompatPlugin {
    private boolean loaded = false;

    /**
     * @return the {@link Info} of the plugin, should always be the same for a single plugin
     */
    public abstract Info getPluginInfo();

    public final boolean isLoaded() {
        return loaded;
    }

    /**
     * This method is called when the mod gets initialized<p>
     * Register everything that must be registered once here
     */
    public void onRegister() {
    }

    /**
     * This method is called everytime the plugin is enabled/reenabled<p>
     * Call everything that can be unloaded in {@link CompatPlugin#onUnload()} here
     */
    public void onLoad() {
        this.loaded = true;
    }

    /**
     * This method is called everytime the plugin disabled<p>
     * You can unload things you have loaded in {@link CompatPlugin#onLoad()} here
     */
    public void onUnload() {
        this.loaded = false;
    }

    /**
     * This event gets raised whenever a packet is sent from the client to the server
     *
     * @param packet The packet object, look inside the packet package for available packet types
     * @param <T>    The type of the packet
     */
    public <T extends CustomPacketPayload> void onPacketSendToServer(T packet) {
    }

    public record Info(ResourceLocation id, String name, String version) {
    }
}
