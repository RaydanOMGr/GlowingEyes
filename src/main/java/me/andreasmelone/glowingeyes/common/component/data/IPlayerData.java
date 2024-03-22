package me.andreasmelone.glowingeyes.common.component.data;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public interface IPlayerData extends Component {
    boolean hasMod();
    void setHasMod(boolean hasMod);
    Set<Player> trackedBy();
    void addTrackedBy(Player player);
    void removeTrackedBy(Player player);
}
