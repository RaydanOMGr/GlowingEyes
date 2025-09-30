package me.andreasmelone.glowingeyes.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.function.Function;

public abstract class AbstractClientCommand<T extends SharedSuggestionProvider> {
    private final Function<String, LiteralArgumentBuilder<T>> argumentBuilderFunction;

    public AbstractClientCommand(Function<String, LiteralArgumentBuilder<T>> argumentBuilderFunction) {
        this.argumentBuilderFunction = argumentBuilderFunction;
    }

    public abstract void register(ClientModContext mod, CommandDispatcher<T> dispatcher);

    protected LiteralArgumentBuilder<T> literal(String name) {
        return argumentBuilderFunction.apply(name);
    }
}
