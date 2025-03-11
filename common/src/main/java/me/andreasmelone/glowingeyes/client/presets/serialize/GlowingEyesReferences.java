package me.andreasmelone.glowingeyes.client.presets.serialize;

import com.mojang.datafixers.DSL;

public class GlowingEyesReferences {
    public static final DSL.TypeReference PRESET = () -> "preset";
    public static final DSL.TypeReference PRESET_FILE = () -> "preset_file";
}
