package me.andreasmelone.glowingeyes.client.presets.serialize.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import me.andreasmelone.glowingeyes.client.presets.serialize.GlowingEyesReferences;

public class IdRemovedFix extends DataFix {
    private final String name;

    public IdRemovedFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
        this.name = "Id Removed Fix v" + outputSchema.getVersionKey();
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(GlowingEyesReferences.PRESET);
        return this.fixTypeEverywhereTyped(this.name, type, typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            if (dynamic.get("id").result().isPresent()) {
                dynamic = dynamic.remove("id");
            }
            return dynamic;
        }));
    }
}