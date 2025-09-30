package me.andreasmelone.glowingeyes.common.util;

public class LoaderUtils {
    public static String LOADER_NAME = "vanilla";
    public static String MOD_VERSION = "undefined";
    public static ModData[] LOADED_MODS = null;

    public record ModData(String id, String name, String version) {
    }
}
