package me.andreasmelone.glowingeyes.client.presets.serialize.fix;

import com.google.gson.JsonElement;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import me.andreasmelone.glowingeyes.client.presets.serialize.GlowingEyesCodecs;
import me.andreasmelone.glowingeyes.client.presets.serialize.GlowingEyesReferences;
import me.andreasmelone.glowingeyes.common.util.Point;
import org.slf4j.Logger;

import java.util.Optional;

public class PointRangeFix extends DataFix {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final String name;
    public PointRangeFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
        this.name = "Point Range Fix v" + outputSchema.getVersionKey();
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(GlowingEyesReferences.PRESET);
        return this.fixTypeEverywhereTyped(this.name, type, typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Dynamic<?> map = dynamic.get("content").orElseEmptyMap().updateMapValues(pair -> {
                return pair.mapFirst((key) -> {
                    Optional<String> result = key.asString().result();
                    if(result.isPresent()) {
                        Point oldPoint = Point.CODEC_STRING.decode(key).map(Pair::getFirst).result().orElseThrow();
                        Point newPoint = new Point(oldPoint.getX() + 8, oldPoint.getY() + 8);
                        Optional<JsonElement> serializedPoint = Point.CODEC_STRING.encodeStart(JsonOps.INSTANCE, newPoint).result();
                        if(serializedPoint.isPresent()) {
                            return dynamic.createString(serializedPoint.get().getAsString());
                        }
                    }
                    return key;
                });
            });

            return dynamic.remove("content").set("content", map);
        }));
    }
}
