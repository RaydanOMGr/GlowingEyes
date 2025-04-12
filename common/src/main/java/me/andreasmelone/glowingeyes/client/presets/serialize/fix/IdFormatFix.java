package me.andreasmelone.glowingeyes.client.presets.serialize.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.presets.serialize.GlowingEyesReferences;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class IdFormatFix extends DataFix {
    private final String name;

    public IdFormatFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
        this.name = "Id Format Fix v" + outputSchema.getVersionKey();
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(GlowingEyesReferences.PRESET);
        return this.fixTypeEverywhereTyped(this.name, type, typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<String> name = dynamic.get("name").asString().result();
            if(dynamic.get("id").result().isPresent() && name.isPresent()) {
                ResourceLocation newId = new ResourceLocation(GlowingEyes.MOD_ID, name.get().toLowerCase().replace(" ", "_"));
                dynamic = dynamic.remove("id").set("id", dynamic.createString(newId.toString()));
            }
            return dynamic;
        }));
    }
}
