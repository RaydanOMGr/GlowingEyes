package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.client.commands.EyesCommand;
import me.andreasmelone.glowingeyes.common.CommonProxy;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.awt.*;
import java.util.HashMap;

public class ClientProxy extends CommonProxy {
    HashMap<Point, Color> pixelMap = new HashMap<>();
    Color color = ModInfo.DEFAULT_EYE_COLOR;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        e.getModLog().info("Registering commands");
        ClientCommandHandler.instance.registerCommand(new EyesCommand());
        ClientCommandHandler.instance.getCommands().forEach((s, command) -> e.getModLog().info("Registered command: " + s));
        e.getModLog().info("Registered commands");
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new ClientGlowingEyesEvents());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
//        new ScheduledTask() {
//            @Override
//            public void run() {
//                GlowingEyes.logger.info("Pixel map: " + pixelMap);
//                if(Minecraft.getMinecraft().player != null) {
//                    GlowingEyes.logger.info("Player cap: " +
//                            Minecraft.getMinecraft().player.getCapability(GlowingEyesProvider.CAPABILITY, EnumFacing.UP)
//                                    .getGlowingEyesMap()
//                    );
//                }
//            }
//        }.runEvery(getScheduler(), 60);

        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        for (RenderPlayer renderPlayer : manager.getSkinMap().values()) {
            renderPlayer.addLayer(new GlowingEyesHeadLayer(renderPlayer));
        }
    }

    @Override
    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }
    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public void openGui(GuiScreen gui) {
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    @Override
    public HashMap<Point, Color> getPixelMap() {
        return pixelMap;
    }

    @Override
    public void setPixelMap(HashMap<Point, Color> pixelMap) {
        this.pixelMap = pixelMap;
    }

    @Override
    public Color getPixelColor() {
        return color;
    }

    @Override
    public void setPixelColor(Color color) {
        this.color = color;
    }
}
