package me.andreasmelone.glowingeyes.client.gui.preset;

import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.HashMap;

public class PresetsScreen extends Screen {
    private int guiLeft, guiTop;
    private int middleX, middleY;

    protected int xSize = 256; // the size of the texture is 256x222
    protected int ySize = 222; // the size of the texture is 256x222

    int page = 0;
    int offset = 0;

    int selectedPreset = -1;
    int pageSize;

    boolean toggledState = true;
    boolean isLocked = false;

    HashMap<Point, Color> savedPixelMap = new HashMap<>();
    private Screen parent;
    private final PresetManager presetManager = PresetManager.getInstance();

    public PresetsScreen() {
        super(Component.empty());
    }

    public PresetsScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    public void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.middleX = this.guiLeft + this.xSize / 2;
        this.middleY = this.guiTop + this.ySize / 2;

        pageSize = 5;

        int leftButtonX = (int) (this.guiLeft + 128 * ((double)1 / 4));
        int rightButtonX = (int) (this.guiLeft + 128 * ((double)3 / 4));

        this.addRenderableWidget(new Button(
                leftButtonX, this.guiTop - 20 + ((pageSize + 1) * 30),
                20, 20,
                Component.literal("<"),
                button -> {
                }
        ));
        this.addRenderableWidget(new Button(
                rightButtonX, this.guiTop - 20 + ((pageSize + 1) * 30),
                20, 20,
                Component.literal(">"),
                button -> {
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + this.xSize - 90, this.guiTop + 120,
                80, 20,
                Component.translatable("gui.done"),
                button -> {
                    if(parent != null) {
                        this.getMinecraft().setScreen(parent);
                    }
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + this.xSize - 90, this.guiTop + 142,
                80, 20,
                Component.translatable("gui.cancel"),
                button -> {
                    if(parent != null) {
                        this.getMinecraft().setScreen(parent);
                    }
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + 10, this.guiTop + 5 + ((pageSize + 1) * 30),
                128 / 2 - 3, 20,
                Component.translatable("gui.presets.create"),
                button -> {
                    this.getMinecraft().setScreen(new EditPresetScreen(this));
                }
        ));
    }
}
