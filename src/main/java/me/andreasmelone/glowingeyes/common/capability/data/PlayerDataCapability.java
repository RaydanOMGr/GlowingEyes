package me.andreasmelone.glowingeyes.common.capability.data;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDataCapability {
    private static Set<UUID> playersWithMod = new HashSet<>();
    protected static final Capability<IPlayerData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IPlayerData.class);
    }

    private PlayerDataCapability() {
    }

    static PlayerDataImpl localCapability = new PlayerDataImpl();
    public static IPlayerData getCapability(Player player) {
        if(player.isLocalPlayer()) return localCapability;
        return player.getCapability(INSTANCE)
                .orElseThrow(() -> new IllegalStateException("Could not get PlayerData capability from player"));
    }

    public static boolean hasMod(Player player) {
        return getCapability(player).hasMod();
    }

    public static void setHasMod(Player player, boolean hasMod) {
        getCapability(player).setHasMod(hasMod);
    }

    public static void addTrackingPlayer(Player player, UUID uuid) {
        getCapability(player).addTrackingPlayer(uuid);
    }

    public static void removeTrackingPlayer(Player player, UUID uuid) {
        getCapability(player).removeTrackingPlayer(uuid);
    }

    public static Set<UUID> getTrackingPlayers(Player player) {
        return getCapability(player).getTrackingPlayers();
    }

    public static void addPlayerWithMod(Player player) {
        playersWithMod.add(player.getUUID());
    }

    public static void removePlayerWithMod(Player player) {
        playersWithMod.remove(player.getUUID());
    }

    public static Set<UUID> getPlayersWithMod() {
        return new HashSet<>(playersWithMod);
    }

    public static void setPlayersWithMod(Set<UUID> players) {
        playersWithMod = new HashSet<>(players);
    }
}
