package me.andreasmelone.glowingeyes.neoforge.common.service;

import me.andreasmelone.glowingeyes.common.util.LoaderUtils;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgeLoaderUtils implements LoaderUtils.Service {
    @Override
    public boolean isModLoaded(String id) {
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(id) != null;
    }
}