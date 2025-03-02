package me.andreasmelone.glowingeyes.client.mod;

import me.andreasmelone.glowingeyes.common.mod.ModContext;

public interface ClientModContext extends ModContext {
    ClientModVariables getModVariables();
}
