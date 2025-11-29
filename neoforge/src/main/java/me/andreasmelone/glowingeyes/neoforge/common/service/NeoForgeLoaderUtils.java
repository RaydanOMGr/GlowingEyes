package me.andreasmelone.glowingeyes.neoforge.common.service;

import me.andreasmelone.glowingeyes.common.util.LoaderUtils;
import net.neoforged.fml.ModList;

public class NeoForgeLoaderUtils implements LoaderUtils.Service {
    @Override
    public boolean isModLoaded(String id) {
        if(ModList.get() == null) return true; // just assume the worst
        return ModList.get().isLoaded(id);
    }
}