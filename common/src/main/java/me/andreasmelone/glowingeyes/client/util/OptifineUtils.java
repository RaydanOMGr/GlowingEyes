package me.andreasmelone.glowingeyes.client.util;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OptifineUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final boolean IS_OPTIFINE_PRESENT;

    private static Method getShaderPackName = null;

    public static String getShaderName() {
        try {
            if (getShaderPackName != null) return (String) getShaderPackName.invoke(null);

            Class<?> clazz = Class.forName("net.optifine.shaders.Shaders");
            getShaderPackName = clazz.getDeclaredMethod("getShaderPackName");
            return (String) getShaderPackName.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            LOGGER.info("Failed to invoke Shaders#getShaderPackName()Ljava/lang/String;", e);
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }

    static {
        boolean isOptifine = false;
        try {
            Class.forName("net.optifine.Config");
            isOptifine = true;
        } catch (ClassNotFoundException ignored) {
        }
        IS_OPTIFINE_PRESENT = isOptifine;
    }
}
