package me.andreasmelone.glowingeyes.client.presets.serialize.fix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.andreasmelone.glowingeyes.client.presets.Preset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record PresetFile(int dataVersion, List<Preset> presets) {
    public static final Codec<PresetFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("DataVersion").forGetter((p) -> Optional.of(p.dataVersion())),
            Codec.list(Preset.CODEC).fieldOf("presets").forGetter(PresetFile::presets)
    ).apply(instance, (version, presets) -> new PresetFile(version.orElse(0), presets)));

    @Override
    public List<Preset> presets() {
        return new ArrayList<>(presets);
    }
}
