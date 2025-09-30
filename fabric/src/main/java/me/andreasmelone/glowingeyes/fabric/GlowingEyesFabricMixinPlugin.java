package me.andreasmelone.glowingeyes.fabric;

import me.andreasmelone.glowingeyes.GlowingEyes;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GlowingEyesFabricMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String[] splitPackage = mixinClassName.split("\\.");
        String[] splitWords = splitPackage[splitPackage.length - 1].split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
        if ("compat".equalsIgnoreCase(splitPackage[splitPackage.length - 2])) {
            return FabricLoader.getInstance().isModLoaded(splitWords[0].toLowerCase());
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (GlowingEyes.DEBUG) {
            String[] splitPackage = mixinClassName.split("\\.");
            if ("compat".equalsIgnoreCase(splitPackage[splitPackage.length - 2])) {
                System.out.println("[Glowing Eyes] Applying compat mixin " + mixinClassName + " to class " + targetClassName);
            }
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
