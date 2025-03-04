package me.andreasmelone.glowingeyes.common.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;

import java.util.function.Function;

public class GlowingEyesCodecs {
    public static <T> Codec<T> withAlternative(final Codec<T> primary, final Codec<? extends T> alternative) {
        return Codec.either(
                primary,
                alternative
        ).xmap(
                (either) -> either.map(Function.identity(), Function.identity()),
                Either::left
        );
    }

    public static <A, T> A decodeQuick(Decoder<A> decoder, DynamicOps<T> ops, T input) {
        return decoder.parse(ops, input)
                .result()
                .orElse(null);
    }
}