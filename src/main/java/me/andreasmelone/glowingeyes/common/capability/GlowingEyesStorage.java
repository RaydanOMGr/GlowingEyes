package me.andreasmelone.glowingeyes.common.capability;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.io.IOException;
import java.util.HashMap;

public class GlowingEyesStorage implements Capability.IStorage<IGlowingEyesCapability> {
    @Override
    public NBTBase writeNBT(Capability<IGlowingEyesCapability> capability, IGlowingEyesCapability instance, EnumFacing side) {
        try {
            return new NBTTagByteArray(Util.serializeHashMap(instance.getGlowingEyesMap()));
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
        if (data.length % 3 != 0) {
            instance.setGlowingEyesMap(new HashMap<>());

            GlowingEyes.logger.error("GlowingEyesStorage: readNBT: data.length % 3 != 0");
            GlowingEyes.logger.error("The format of the byte array is not correct and most likely corrupted");
            GlowingEyes.logger.error("Data will be changed to default");
            return;
        }

        try {
            instance.setGlowingEyesMap(Util.deserializeHashMap(data));
        } catch (IOException | ClassNotFoundException e) {
            GlowingEyes.logger.error("GlowingEyesStorage: Failed reading NBT");
            e.printStackTrace();
            GlowingEyes.logger.error("Data will be changed to default");
            instance.setGlowingEyesMap(new HashMap<>());
        }
    }
}