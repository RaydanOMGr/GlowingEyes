package me.andreasmelone.glowingeyes.common.capability;

import me.andreasmelone.glowingeyes.GlowingEyes;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.awt.*;
import java.util.HashMap;

public class GlowingEyesStorage implements Capability.IStorage<IGlowingEyesCapability> {
    @Override
    public NBTBase writeNBT(Capability<IGlowingEyesCapability> capability, IGlowingEyesCapability instance, EnumFacing side) {
        if(side == EnumFacing.UP) {
            // write a hashmap, somehow
            byte[] data = new byte[instance.getGlowingEyesMap().size() * 6];
            int i = 0;
            for(Point point : instance.getGlowingEyesMap().keySet()) {
                data[i] = (byte) point.x;
                data[i + 1] = (byte) point.y;
                data[i + 2] = (byte) instance.getGlowingEyesMap().get(point).getRed();
                data[i + 3] = (byte) instance.getGlowingEyesMap().get(point).getGreen();
                data[i + 4] = (byte) instance.getGlowingEyesMap().get(point).getBlue();
                data[i + 5] = (byte) instance.getGlowingEyesMap().get(point).getAlpha();
                i += 6;
            }
            return new NBTTagByteArray(data);
        } else {
            return new NBTTagCompound();
        }
    }

    @Override
    public void readNBT(Capability<IGlowingEyesCapability> capability, IGlowingEyesCapability instance, EnumFacing side, NBTBase nbt) {
        if(side == EnumFacing.UP) {
            if(!(nbt instanceof NBTTagByteArray)) {
                instance.setGlowingEyesMap(new HashMap<>());

                GlowingEyes.logger.error("GlowingEyesStorage: readNBT: nbt is not NBTTagByteArray");
                GlowingEyes.logger.error("The format of GlowingEyes has been changed");
                GlowingEyes.logger.error("Data will be changed to default");
                return;
            }
            byte[] data = ((NBTTagByteArray) nbt).getByteArray();
            if(data.length % 6 != 0) {
                instance.setGlowingEyesMap(new HashMap<>());

                GlowingEyes.logger.error("GlowingEyesStorage: readNBT: data.length % 6 != 0");
                GlowingEyes.logger.error("The format of the byte array is not correct and most likely corrupted");
                GlowingEyes.logger.error("Data will be changed to default");
                return;
            }
            HashMap<Point, Color> glowingEyesMap = new HashMap<>();
            for(int i = 0; i < data.length; i += 6) {
                Point point = new Point(data[i], data[i + 1]);
                Color color = new Color(data[i + 2], data[i + 3], data[i + 4], data[i + 5]);
                glowingEyesMap.put(point, color);
            }
            instance.setGlowingEyesMap(glowingEyesMap);
        }
    }
}