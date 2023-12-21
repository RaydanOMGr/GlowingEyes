package me.andreasmelone.glowingeyes.common.capability;

import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashMap;

public class GlowingEyesStorage implements Capability.IStorage<IGlowingEyesCapability> {
    @Override
    public NBTBase writeNBT(Capability<IGlowingEyesCapability> capability, IGlowingEyesCapability instance, EnumFacing side) {
        GlowingEyes.logger.info("GlowingEyesStorage: writeNBT: side = " + side);
        if(side == EnumFacing.UP) {
            // write a hashmap, somehow
            return new NBTTagByteArray(instance.getGlowingEyesMapBytes());
        } else {
            return new NBTTagCompound();
        }
    }

    @Override
    public void readNBT(Capability<IGlowingEyesCapability> capability, IGlowingEyesCapability instance, EnumFacing side, NBTBase nbt) {
        GlowingEyes.logger.info("GlowingEyesStorage: readNBT: side = " + side);
        if(side == EnumFacing.UP) {
            if(!(nbt instanceof NBTTagByteArray)) {
                instance.setGlowingEyesMap(new HashMap<>());

                GlowingEyes.logger.error("GlowingEyesStorage: readNBT: nbt is not NBTTagByteArray");
                GlowingEyes.logger.error("The format of GlowingEyes has been changed");
                GlowingEyes.logger.error("Data will be changed to default");
                return;
            }
            byte[] data = ((NBTTagByteArray) nbt).getByteArray();
            if(data.length % 3 != 0) {
                instance.setGlowingEyesMap(new HashMap<>());

                GlowingEyes.logger.error("GlowingEyesStorage: readNBT: data.length % 3 != 0");
                GlowingEyes.logger.error("The format of the byte array is not correct and most likely corrupted");
                GlowingEyes.logger.error("Data will be changed to default");
                return;
            }

            instance.setGlowingEyesMapBytes(data);

        } else {
            GlowingEyes.logger.error("GlowingEyesStorage: readNBT: side != EnumFacing.UP");
            GlowingEyes.logger.error("The format of GlowingEyes has been changed");
            GlowingEyes.logger.error("Data will be changed to default");
            instance.setGlowingEyesMap(new HashMap<>());
        }
    }
}