package me.andreasmelone.glowingeyes.neoforge.client.compat;

import me.andreasmelone.glowingeyes.client.compat.CompatPluginRegistry;
import me.andreasmelone.glowingeyes.neoforge.client.compat.epicfight.EpicFightCompatPlugin;

public class NeoForgeCompatPlugins {
    public static void init() {
        try {
            CompatPluginRegistry.register(new EpicFightCompatPlugin(), true);
        } catch (NoClassDefFoundError ignored) {
            // this means that the classes required to init the plugin are not present
        }
    }
}
