package me.andreasmelone.glowingeyes.client.ui;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.presets.Preset;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class PresetsScreen extends GuiScreen {
    private int guiLeft;
    private int guiTop;

    protected int xSize = 256; // the size of the texture is 221x222
    protected int ySize = 222; // the size of the texture is 221x222

    private final GuiScreen parent;

    public PresetsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiUtil.drawBackground(TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        List<Preset> presets = GlowingEyes.proxy.getPresetManager().getPresets();
        int i = 0;
        for(Preset preset : presets) {
            this.mc.getTextureManager().bindTexture(preset.getResourceLocation());
            // draw a grey background
            this.drawGradientRect(this.guiLeft + 10 + (i * 50), this.guiTop + 10, this.guiLeft + 50 + (i * 50), this.guiTop + 50, 0x80FFFFFF, 0x80FFFFFF);
            // now draw eyes
            this.drawTexturedModalRect(this.guiLeft + 10 + (i * 50), this.guiTop + 10, 0, 0, 40, 40);
            i++;
        }
    }

    @Override
    public void onGuiClosed() {
        this.mc.displayGuiScreen(this.parent);
    }
}
