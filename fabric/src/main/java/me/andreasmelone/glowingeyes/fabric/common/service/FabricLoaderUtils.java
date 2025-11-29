package me.andreasmelone.glowingeyes.fabric.common.service;

import me.andreasmelone.glowingeyes.common.util.LoaderUtils;
import net.fabricmc.loader.api.FabricLoader;

public class FabricLoaderUtils implements LoaderUtils.Service {
    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }
}
