package me.andreasmelone.glowingeyes.client.util;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class IrisUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean IS_IRIS_PRESENT = false;

    private static Object config = null;
    private static Method getShaderPackName = null;

    @SuppressWarnings("unchecked")
    public static String getShaderName() {
        final String[] irisMainClass = new String[]{"net.irisshaders.iris.Iris", "net.coderbot.iris.Iris"};

        for (String irisClass : irisMainClass) {
            try {
                if (getShaderPackName != null && config != null) return ((Optional<String>) getShaderPackName.invoke(config)).orElse(null);

                Class<?> clazz = Class.forName(irisClass);
                Field configField = clazz.getDeclaredField("irisConfig");
                configField.setAccessible(true);
                config = configField.get(null);
                getShaderPackName = config.getClass().getDeclaredMethod("getShaderPackName");
                return ((Optional<String>) getShaderPackName.invoke(config)).orElse(null);
            } catch (NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                LOGGER.info("Failed to invoke IrisConfig#getShaderPackName()Ljava/util/Optional;", e);
            } catch (ClassNotFoundException | NoSuchFieldException ignored) {
            }
        }
        return null;
    }
}
