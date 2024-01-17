package me.andreasmelone.glowingeyes.common.capability.eyes;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class GlowingEyesStorage implements Capability.IStorage<IGlowingEyesCapability> {
    @Override
    public NBTBase writeNBT(Capability<IGlowingEyesCapability> capability, IGlowingEyesCapability instance, EnumFacing side) {
        try {
            byte[] data = Util.serializeHashMap(instance.getGlowingEyesMap());
            ByteBuffer buffer = ByteBuffer.allocate(data.length + 1);
            buffer.put((byte) (instance.isToggledOn() ? 1 : 0));
            buffer.put(data);
            return new NBTTagByteArray(buffer.array());
        } catch (IOException e) {
            GlowingEyes.logger.error("GlowingEyesStorage: Failed writing NBT");
            e.printStackTrace();
            return new NBTTagCompound();
        }
    }

    @Override
    public void readNBT(Capability<IGlowingEyesCapability> capability, IGlowingEyesCapability instance, EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagByteArray)) {
            instance.setGlowingEyesMap(new HashMap<>());

            GlowingEyes.logger.error("GlowingEyesStorage: readNBT: nbt is not NBTTagByteArray");
            GlowingEyes.logger.error("The format of GlowingEyes NBT has been changed");
            GlowingEyes.logger.error("Data will be changed to default");
            return;
        }

        byte[] data = ((NBTTagByteArray) nbt).getByteArray();

        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte toggledOn = buffer.get();
        instance.setToggledOn(toggledOn == (byte)1);

        byte[] mapData = new byte[data.length - 1];

        try {
            buffer.get(mapData);
            instance.setGlowingEyesMap(Util.deserializeHashMap(mapData));
        } catch (BufferUnderflowException | BufferOverflowException | IOException | ClassNotFoundException e) {
            instance.setGlowingEyesMap(new HashMap<>());

            GlowingEyes.logger.error("GlowingEyesStorage: readNBT: Failed reading NBT");
            e.printStackTrace();
            GlowingEyes.logger.error("Data will be changed to default");
        }
    }
}