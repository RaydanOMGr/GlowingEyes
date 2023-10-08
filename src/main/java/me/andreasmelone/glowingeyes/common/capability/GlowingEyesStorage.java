package me.andreasmelone.glowingeyes.common.capability;

import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class GlowingEyesStorage implements Capability.IStorage<IGlowingEyesCapability> {
    @Override
    public NBTBase writeNBT(Capability<IGlowingEyesCapability> capability, IGlowingEyesCapability instance, EnumFacing side) {
        if(side == EnumFacing.UP) {
            return new NBTTagString(instance.getRawString());
        } else {
            return new NBTTagCompound();
        }
    }

    @Override
    public void readNBT(Capability<IGlowingEyesCapability> capability, IGlowingEyesCapability instance, EnumFacing side, NBTBase nbt) {
        if(side == EnumFacing.UP) {
            if(!(nbt instanceof NBTTagString)) {
                instance.setHasGlowingEyes(false);
                instance.setGlowingEyesType(0);
                if(nbt instanceof NBTTagInt) {
                    instance.setGlowingEyesType(((NBTTagInt) nbt).getInt());
                }
                GlowingEyes.logger.warn("GlowingEyesStorage: readNBT: nbt is not NBTTagString");
                GlowingEyes.logger.warn("This nbt tag will be ignored and the default value will be used.");
                GlowingEyes.logger.warn("The nbt tag will be written correctly next time.");
                return;
            }
            String[] data = ((NBTTagString) nbt).getString().split(":");

            instance.setGlowingEyesType(Integer.parseInt(data[1]));
            instance.setHasGlowingEyes(Boolean.parseBoolean(data[0]));
        }
    }
}