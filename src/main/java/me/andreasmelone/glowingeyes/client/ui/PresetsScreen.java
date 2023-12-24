package me.andreasmelone.glowingeyes.client.ui;

import net.minecraft.client.gui.GuiScreen;

public class PresetsScreen extends GuiScreen {
    private final GuiScreen parent;
    public PresetsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void onGuiClosed() {
        this.mc.displayGuiScreen(this.parent);
    }
}
