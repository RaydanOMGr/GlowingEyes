package me.andreasmelone.glowingeyes.fabric;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.IrisUtils;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.LoaderUtils;
import me.andreasmelone.glowingeyes.fabric.common.GlowingEyesEvents;
import me.andreasmelone.glowingeyes.fabric.common.component.data.PlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.fabric.common.component.eyes.GlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.fabric.common.packet.ServerPacketRegistrar;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Collection;

public class GlowingEyesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        GlowingEyesComponent.setImplementation(new GlowingEyesComponentImpl());
        PlayerDataComponent.setImplementation(new PlayerDataComponentImpl());

        ServerPacketRegistrar.registerServerPackets();
        GlowingEyesEvents.registerEvents();

        LoaderUtils.LOADER_NAME = "Fabric";
        Collection<ModContainer> allMods = FabricLoader.getInstance().getAllMods();
        LoaderUtils.LOADED_MODS = new LoaderUtils.ModData[allMods.size()];
        int i = 0;
        for (ModContainer mod : allMods) {
            if (mod.getMetadata().getId().equalsIgnoreCase(GlowingEyes.MOD_ID)) {
                LoaderUtils.MOD_VERSION = mod.getMetadata().getVersion().getFriendlyString();
            }

            LoaderUtils.LOADED_MODS[i] = new LoaderUtils.ModData(
                    mod.getMetadata().getId(),
                    mod.getMetadata().getName(),
                    mod.getMetadata().getVersion().getFriendlyString()
            );
            i++;
        }

        IrisUtils.IS_IRIS_PRESENT = FabricLoader.getInstance().isModLoaded("iris");
    }
}
