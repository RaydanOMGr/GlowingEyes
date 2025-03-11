package me.andreasmelone.glowingeyes.client.presets.serialize;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import java.util.Map;
import java.util.function.Supplier;

public class SchemaV0 extends Schema {
    public SchemaV0(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        return Maps.newHashMap();
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        return Maps.newHashMap();
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        schema.registerType(true, GlowingEyesReferences.PRESET, DSL::remainder);
        schema.registerType(true, GlowingEyesReferences.PRESET_FILE, DSL::remainder);
    }
}
