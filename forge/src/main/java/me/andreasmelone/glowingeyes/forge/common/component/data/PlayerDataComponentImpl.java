package me.andreasmelone.glowingeyes.forge.common.component.data;

import me.andreasmelone.glowingeyes.common.component.data.IPlayerDataComponent;
import me.andreasmelone.glowingeyes.forge.common.packets.HasModPacket;
import me.andreasmelone.glowingeyes.forge.common.packets.PacketManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Set;

public class PlayerDataComponentImpl implements IPlayerDataComponent {
    protected static final Capability<IPlayerData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IPlayerData.class);
    }

    IPlayerData localComponent = new PlayerDataImpl();

    public IPlayerData getComponent(Player player) {
        if(player.isLocalPlayer()) return localComponent;
        return player.getCapability(INSTANCE)
                .orElseThrow(() -> new IllegalStateException("Could not get PlayerData capability from player"));
    }

    @Override
    public boolean hasMod(Player player) {
        return getComponent(player).hasMod();
    }

    @Override
    public void setHasMod(Player player, boolean hasMod) {
        getComponent(player).setHasMod(hasMod);
    }

    @Override
    public Set<Player> getTrackedBy(Player player) {
        return getComponent(player).trackedBy();
    }

    @Override
    public void addTrackedBy(Player playerTracked, Player trackedBy) {
        getComponent(playerTracked).addTrackedBy(trackedBy);
    }

    @Override
    public void removeTrackedBy(Player playerTracked, Player trackedBy) {
        getComponent(playerTracked).removeTrackedBy(trackedBy);
    }

    @Override
    public void sendUpdate(ServerPlayer player) {
        PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(() -> player);
        PacketManager.INSTANCE.send(target, new HasModPacket());
    }
}
