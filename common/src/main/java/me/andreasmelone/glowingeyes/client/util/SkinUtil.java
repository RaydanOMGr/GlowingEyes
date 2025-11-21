package me.andreasmelone.glowingeyes.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.PlayerModelType;

public class SkinUtil {
    public static boolean isSlim() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return false;
        return player.getSkin().model() == PlayerModelType.SLIM;
    }
}
