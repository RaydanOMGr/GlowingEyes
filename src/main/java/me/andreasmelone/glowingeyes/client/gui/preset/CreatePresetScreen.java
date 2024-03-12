package me.andreasmelone.glowingeyes.client.gui.preset;

import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;

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
        nameField.setFocus(true);

        this.addRenderableWidget(new Button(
                this.guiLeft + 20, this.guiTop + 100,
                80 - 5, 20,
                Component.translatable("gui.create"),
                button -> {
                    PresetManager.getInstance().createPreset(nameField.getValue(), GlowingEyesCapability.getGlowingEyesMap(getMinecraft().player));
                    getMinecraft().setScreen(parent);
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + 100 + (5 * 2), this.guiTop + 100,
                80 - 5, 20,
                Component.translatable("gui.cancel"),
                button -> {
                    getMinecraft().setScreen(parent);
                }
        ));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        parent.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderBackground(poseStack);
        GuiUtil.drawBackground(
                poseStack, TextureLocations.UI_BACKGROUND_SLIM,
                this.guiLeft, this.guiTop,
                this.xSize, this.ySize
        );

        drawCenteredString(
                poseStack, this.font,
                Component.translatable("gui.create.title"),
                this.width / 2, this.guiTop + 10,
                0xFFFFFF
        );

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}