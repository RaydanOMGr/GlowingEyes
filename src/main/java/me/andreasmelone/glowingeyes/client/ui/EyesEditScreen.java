package me.andreasmelone.glowingeyes.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

// uhh, I'm still trying to implement this, don't mind me
public class EyesEditScreen extends GuiScreen {
    private final int DIALOGUE_WIDTH = 212 * 2;
    private final int DIALOGUE_HEIGHT = 212;

    private int guiLeft;
    private int guiTop;

    protected int xSize = 176;
    protected int ySize = 166;

    private Minecraft mc;

    public EyesEditScreen(Minecraft mc) {
        super();
        this.mc = mc;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;


    }
}
