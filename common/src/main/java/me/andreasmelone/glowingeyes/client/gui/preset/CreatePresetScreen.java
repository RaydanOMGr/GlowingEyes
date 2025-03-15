package me.andreasmelone.glowingeyes.client.gui.preset;

import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CreatePresetScreen extends Screen {
    Screen parent;

    private final int xSize = 200;
    private final int ySize = 143;

    public CreatePresetScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    int guiLeft;
    int guiTop;

    EditBox nameField;

    @Override
    public void init() {
        super.init();
        if(parent != null) parent.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addRenderableWidget(nameField =
                new EditBox(this.font,
                        this.guiLeft + 20, this.guiTop + 40,
                        this.xSize - (20 * 2), 20,
                        Component.empty()
                ));
        nameField.setFocus(true);

        this.addRenderableWidget(
            GuiUtil.buttonBuilder(
                Component.translatable("gui.glowingeyes.create"),
                    button -> {
                        PresetManager.getInstance().createPreset(nameField.getValue(), GlowingEyesComponent.getGlowingEyesMap(Minecraft.getInstance().player));
                        Minecraft.getInstance().setScreen(parent);
                }
            ).pos(this.guiLeft + 20, this.guiTop + 100)
            .size(80 - 5, 20)
            .build()
        );
        this.addRenderableWidget(
            GuiUtil.buttonBuilder(
                Component.translatable("gui.glowingeyes.cancel"),
                button -> {
                    Minecraft.getInstance().setScreen(parent);
                }
            ).pos(this.guiLeft + 100 + (5 * 2), this.guiTop + 100)
            .size(80 - 5, 20)
            .build()
        );
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if(parent != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0, -100);
            parent.render(poseStack, 0, 0, partialTicks);
            poseStack.popPose();
        }
        this.renderBackground(poseStack);
        GuiUtil.drawBackground(poseStack, TextureLocations.UI_BACKGROUND_SLIM, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        drawCenteredString(poseStack, this.font, Component.translatable("gui.glowingeyes.create.title"), this.width / 2, this.guiTop + 10, 0xFFFFFF);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        if(parent != null) {
            Minecraft.getInstance().setScreen(parent);
            parent.init(
                    Minecraft.getInstance(),
                    Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                    Minecraft.getInstance().getWindow().getGuiScaledHeight()
            );
        }
    }
}