package me.andreasmelone.glowingeyes.fabric.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketContext {
    @NotNull private final PacketFlow direction;
    @Nullable private ServerPlayer sender;
    @Nullable private Minecraft minecraft;

    public PacketContext(@NotNull ServerPlayer sender) {
        this.direction = PacketFlow.SERVERBOUND;
        this.sender = sender;
    }

    public PacketContext(@NotNull Minecraft minecraft) {
        this.direction = PacketFlow.CLIENTBOUND;
        this.minecraft = minecraft;
    }

    /**
     * Returns the direction in which the packet has been sent.
     * CLIENTBOUND means that the packet has been sent to the client and the {@link PacketContext#getMinecraft()} returns a non-null value.
     * SERVERBOUND means that the packet has been sent to the server and the {@link PacketContext#getSender()} returns a non-null value.
     * @return The packet direction
     */
    public @NotNull PacketFlow getDirection() {
        return direction;
    }

    /**
     * Non-null only when {@link PacketContext#getDirection()} == {@link PacketFlow#SERVERBOUND}
     * @return The {@link ServerPlayer} that sent the packet
     */
    public @Nullable ServerPlayer getSender() {
        return sender;
    }

    /**
     * Non-null only when {@link PacketContext#getDirection()} == {@link PacketFlow#CLIENTBOUND}
     * @return The current {@link Minecraft} instance
     */
    public @Nullable Minecraft getMinecraft() {
        return minecraft;
    }

    public void enqueueWork(Runnable runnable) {
        if(direction == PacketFlow.SERVERBOUND) {
            sender.getServer().execute(runnable);
        } else {
            minecraft.execute(runnable);
        }
    }
}
