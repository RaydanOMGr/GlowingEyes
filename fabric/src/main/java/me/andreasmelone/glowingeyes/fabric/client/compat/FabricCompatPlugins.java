package me.andreasmelone.glowingeyes.fabric.client.compat;

import me.andreasmelone.glowingeyes.client.compat.CompatPluginRegistry;
import me.andreasmelone.glowingeyes.fabric.client.compat.flashback.FlashbackCompatPlugin;
import me.andreasmelone.glowingeyes.fabric.client.compat.replaymod.ReplayModCompatPlugin;

public class FabricCompatPlugins {
    public static void init() {
        try {
            CompatPluginRegistry.register(new FlashbackCompatPlugin(), true);
        } catch (NoClassDefFoundError ignored) {
            // this means that the classes required to init the plugin are not present
        }
        try {
            CompatPluginRegistry.register(new ReplayModCompatPlugin(), true);
        } catch (NoClassDefFoundError ignored) {
            // this means that the classes required to init the plugin are not present
        }
    }
}
