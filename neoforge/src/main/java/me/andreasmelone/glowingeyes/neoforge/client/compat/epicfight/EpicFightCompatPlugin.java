package me.andreasmelone.glowingeyes.neoforge.client.compat.epicfight;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.compat.CompatPlugin;
import me.andreasmelone.glowingeyes.common.util.Util;

public class EpicFightCompatPlugin extends CompatPlugin {
    public static final Info INFO = new Info(Util.id(GlowingEyes.MOD_ID, "epic_fight_compat_plugin"), "EpicFight Compat Plugin", "1.0.0");

    @Override
    public Info getPluginInfo() {
        return INFO;
    }
}
