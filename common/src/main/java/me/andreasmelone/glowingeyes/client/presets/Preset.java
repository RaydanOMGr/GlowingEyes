package me.andreasmelone.glowingeyes.client.presets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Preset {
    public static final Codec<Preset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(Preset::getName),
            Codec.unboundedMap(Point.CODEC_STRING, Color.CODEC).fieldOf("content").forGetter(Preset::getContent)
        ).apply(instance, Preset::new));

    private String name;
    private final Map<Point, Color> content;

    public Preset(String name, Map<Point, Color> content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Point, Color> getContent() {
        return new HashMap<>(this.content);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;
        Preset preset = (Preset) o;
        return Objects.equals(this.name, preset.name) && Objects.equals(this.content, preset.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.content);
    }

    @Override
    public String toString() {
        return "Preset{" +
                "name='" + this.name + '\'' +
                ", content=" + this.content +
                '}';
    }
}