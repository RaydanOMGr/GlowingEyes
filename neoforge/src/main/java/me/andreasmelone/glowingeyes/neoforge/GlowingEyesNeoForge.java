package me.andreasmelone.glowingeyes.neoforge;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.IrisUtils;
import me.andreasmelone.glowingeyes.common.util.LoaderUtils;
import me.andreasmelone.glowingeyes.neoforge.client.GlowingEyesClient;
import me.andreasmelone.glowingeyes.neoforge.common.GlowingEyesEvents;
import me.andreasmelone.glowingeyes.neoforge.common.component.ComponentHandler;
import me.andreasmelone.glowingeyes.neoforge.common.packets.PacketHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.slf4j.Logger;

@Mod(GlowingEyes.MOD_ID)
public class GlowingEyesNeoForge {
    private static final Logger LOGGER = LogUtils.getLogger();

    public GlowingEyesNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(PacketHandler::registerPackets);
        modEventBus.register(new GlowingEyesClient(modEventBus));

        ComponentHandler.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Glowing Eyes common setup");
        NeoForge.EVENT_BUS.register(new GlowingEyesEvents());

        LoaderUtils.LOADER_NAME = "NeoForge";
        ModList allMods = ModList.get();
        LoaderUtils.LOADED_MODS = new LoaderUtils.ModData[allMods.size()];
        int i = 0;
        for (ModContainer mod : allMods.getSortedMods()) {
            if (mod.getModId().equalsIgnoreCase(GlowingEyes.MOD_ID)) {
                LoaderUtils.MOD_VERSION = toVersionString(mod.getModInfo().getVersion());
            }

            LoaderUtils.LOADED_MODS[i] = new LoaderUtils.ModData(
                    mod.getModId(),
                    mod.getModInfo().getDisplayName(),
                    toVersionString(mod.getModInfo().getVersion())
            );
            i++;
        }
        IrisUtils.IS_IRIS_PRESENT = ModList.get().isLoaded("oculus");
    }

    private static String toVersionString(ArtifactVersion version) {
        StringBuilder sb = new StringBuilder();

        if (version.getMajorVersion() != 0) {
            sb.append(version.getMajorVersion());
            if (version.getMinorVersion() != 0) {
                sb.append('.').append(version.getMinorVersion());
                if (version.getIncrementalVersion() != 0) {
                    sb.append('.').append(version.getIncrementalVersion());
                }
            }
        }

        if (version.getQualifier() != null && !version.getQualifier().isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append('-');
            }
            sb.append(version.getQualifier());
        } else if (version.getBuildNumber() != 0) {
            if (!sb.isEmpty()) {
                sb.append('-');
            }
            sb.append(version.getBuildNumber());
        }

        return sb.toString();
    }
}
