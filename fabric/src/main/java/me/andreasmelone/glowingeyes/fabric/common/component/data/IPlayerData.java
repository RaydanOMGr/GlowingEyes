package me.andreasmelone.glowingeyes.fabric.common.component.data;

import net.minecraft.world.entity.player.Player;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.Set;

public interface IPlayerData extends Component {
    boolean hasMod();
    void setHasMod(boolean hasMod);
    Set<Player> trackedBy();
    void addTrackedBy(Player player);
    void removeTrackedBy(Player player);
}
