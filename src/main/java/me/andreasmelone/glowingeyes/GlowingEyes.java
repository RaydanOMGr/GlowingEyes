package me.andreasmelone.glowingeyes;

import me.andreasmelone.glowingeyes.common.CommonProxy;
import me.andreasmelone.glowingeyes.common.util.ModInfo;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class GlowingEyes {
    public static Logger logger;
    public static boolean serverHasMod = false;

    @SidedProxy(clientSide = ModInfo.CLIENT_PROXY, serverSide = ModInfo.COMMON_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModLog().info(ModInfo.NAME + " is starting pre initialization!");
        logger = event.getModLog();
        proxy.preInit(event);
        logger.info(ModInfo.NAME + " has finished pre initialization!");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info(ModInfo.NAME + " is starting initialization!");
        proxy.init(event);
        logger.info(ModInfo.NAME + " has finished initialization!");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        logger.info(ModInfo.NAME + " is in post initialization!");
        proxy.postInit(e);
        logger.info(ModInfo.NAME + " has finished post initialization!");
    }
}