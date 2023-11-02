package me.andreasmelone.glowingeyes.client;

import me.andreasmelone.glowingeyes.client.commands.EyesCommand;
import me.andreasmelone.glowingeyes.client.data.DataSaveFile;
import me.andreasmelone.glowingeyes.common.CommonProxy;
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
    private HashMap<Point, Color> pixelMap = new HashMap<>();
    DataSaveFile dataSaveFile;

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

        dataSaveFile = new DataSaveFile();
        dataSaveFile.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
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
    public DataSaveFile getDataSaveFile() {
        return dataSaveFile;
    }

    @Override
    public void openGui(GuiScreen gui) {
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    @Override
    public HashMap<Point, Color> getPixelMap() {
        return pixelMap;
    }
}
