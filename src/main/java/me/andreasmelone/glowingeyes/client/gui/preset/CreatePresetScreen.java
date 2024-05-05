package me.andreasmelone.glowingeyes.client.gui.preset;

import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.server.capability.eyes.GlowingEyesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CreatePresetScreen extends Screen {
    int xSize = 200;
    int ySize = 143;

    Screen parent;
    Minecraft mc;

    public CreatePresetScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    int guiLeft;
    int guiTop;

    EditBox nameField;
    @Override
    public void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addRenderableWidget(nameField = new EditBox(
                this.font,
                this.width / 3, this.guiTop + 40,
                this.xSize - (20 * 2), 20,
                Component.empty()
        ));
        nameField.setFocused(true);

        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.create"),
                button -> {
                    PresetManager.getInstance().createPreset(nameField.getValue(), GlowingEyesCapability.getGlowingEyesMap(Minecraft.getInstance().player));
                    Minecraft.getInstance().setScreen(parent);
                }
        ).pos(this.guiLeft + 20, this.guiTop + 100).size(80 - 5, 20).build());
        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.cancel"),
                button -> {
                    Minecraft.getInstance().setScreen(parent);
                }
        ).pos(this.guiLeft + 100 + (5 * 2), this.guiTop + 100).size(80 - 5, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        parent.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderBackground(guiGraphics);
        GuiUtil.drawBackground(
                guiGraphics, TextureLocations.UI_BACKGROUND_SLIM,
                this.guiLeft, this.guiTop,
                this.xSize, this.ySize
        );

        guiGraphics.drawCenteredString(
                this.font,
                Component.translatable("gui.create.title"),
                this.width / 2, this.guiTop + 10,
                0xFFFFFF
        );

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}