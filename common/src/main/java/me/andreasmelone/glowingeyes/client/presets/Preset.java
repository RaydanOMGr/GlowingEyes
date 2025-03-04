package me.andreasmelone.glowingeyes.client.presets;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Preset {
    public static final Codec<Preset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(Preset::getName),
            Codec.either(Codec.INT, ResourceLocation.CODEC).fieldOf("id").forGetter((p) -> Either.right(p.getId())),
            Codec.unboundedMap(Point.CODEC_STRING, Color.CODEC).fieldOf("content").forGetter(Preset::getContent)
        ).apply(instance,
            (name, id, content) -> new Preset(name, convertId(name, id), content)));

    private String name;
    private final ResourceLocation id;
    private final Map<Point, Color> content;

    public Preset(String name, ResourceLocation id, Map<Point, Color> content) {
        this.name = name;
        this.id = id;
        this.content = content;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Map<Point, Color> getContent() {
        return new HashMap<>(this.content);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Preset preset = (Preset) o;
        return Objects.equals(name, preset.name) && Objects.equals(id, preset.id) && Objects.equals(content, preset.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, content);
    }

    @Override
    public String toString() {
        return "Preset{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", content=" + content +
                '}';
    }

    private static ResourceLocation convertId(String name, Either<Integer, ResourceLocation> idEither) {
        return idEither.map(
                id -> Util.id(GlowingEyes.MOD_ID, name.toLowerCase().replace(" ", "_")),
                resourceLocation -> resourceLocation
        );
    }
}