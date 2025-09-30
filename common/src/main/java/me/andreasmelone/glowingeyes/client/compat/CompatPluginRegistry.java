package me.andreasmelone.glowingeyes.client.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CompatPluginRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, CompatPlugin> PLUGINS = new HashMap<>();

    public static void register(CompatPlugin plugin, boolean load) {
        if (PLUGINS.containsKey(plugin.getPluginInfo().id()))
            throw new IllegalArgumentException("Plugin with ID " + plugin.getPluginInfo().id() + " already registered: " + plugin.getPluginInfo());
        LOGGER.info("Registering compat plugin {} {}", plugin.getPluginInfo().name(), plugin.getPluginInfo().version());
        PLUGINS.put(plugin.getPluginInfo().id(), plugin);
        plugin.onRegister();
        if (load) loadPlugin(plugin.getPluginInfo().id());
    }

    public static void loadPlugin(ResourceLocation id) {
        CompatPlugin plugin = getById(id);
        if (plugin == null) throw new IllegalArgumentException("Plugin " + id + " not found");
        if (plugin.isLoaded()) return;
        LOGGER.info("Loading compat plugin {} {}", plugin.getPluginInfo().name(), plugin.getPluginInfo().version());
        plugin.onLoad();
    }

    public static void unloadPlugin(ResourceLocation id) {
        CompatPlugin plugin = getById(id);
        if (plugin == null) throw new IllegalArgumentException("Plugin " + id + " not found");
        if (!plugin.isLoaded()) return;
        LOGGER.info("Unloading compat plugin {} {}", plugin.getPluginInfo().name(), plugin.getPluginInfo().version());
        plugin.onUnload();
    }

    public static CompatPlugin getById(ResourceLocation id) {
        return PLUGINS.get(id);
    }

    @ApiStatus.Internal
    public static <T extends CustomPacketPayload> void raisePacketSendToServerEvent(T packet) {
        PLUGINS.forEach((id, plugin) -> {
            if (!plugin.isLoaded()) return;
            plugin.onPacketSendToServer(packet);
        });
    }

    public static Set<CompatPlugin> getCompatPlugins() {
        return new HashSet<>(PLUGINS.values());
    }
}
