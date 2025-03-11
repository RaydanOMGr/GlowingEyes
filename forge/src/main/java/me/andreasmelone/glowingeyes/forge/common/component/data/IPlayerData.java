package me.andreasmelone.glowingeyes.forge.common.component.data;

import net.minecraft.world.entity.player.Player;

import java.util.Set;

public interface IPlayerData {
    boolean hasMod();
    void setHasMod(boolean hasMod);
    Set<Player> trackedBy();
    void addTrackedBy(Player player);
    void removeTrackedBy(Player player);
}
