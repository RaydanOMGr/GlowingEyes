package me.andreasmelone.glowingeyes.client.gui;

import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;

public class ConfirmResetScreen extends Screen {
    private int guiLeft;
    private int guiTop;
    private int middleX;
    private int middleY;

    private final int xSize = 200;
    private final int ySize = 143;
    private final Screen parent;
    protected ConfirmResetScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        if(parent != null) parent.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        middleX = this.guiLeft + (xSize / 2);
        middleY = this.guiTop + (ySize / 2);

        int width = (middleX - guiLeft) - 7;
        int confirmX = guiLeft + 5;
        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.glowingeyes.confirm"),
                button -> {
                    if (parent != null) {
                        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, new HashMap<>());
                        ClientGlowingEyesComponent.sendUpdate();
                        Minecraft.getInstance().setScreen(parent);
                    }
                }
            ).pos(confirmX, this.guiTop + this.ySize - 25)
            .size(width, 20)
            .build()
        );

        int cancelX = middleX + 2;
        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.glowingeyes.cancel"),
                button -> {
                    if (parent != null) {
                        Minecraft.getInstance().setScreen(parent);
                    }
                }
            ).pos(cancelX, this.guiTop + this.ySize - 25)
            .size(width, 20)
            .build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(parent != null) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, -100);
            parent.render(guiGraphics, 0, 0, partialTicks);
            guiGraphics.pose().popPose();
        }
        this.renderBackground(guiGraphics);
        GuiUtil.drawBackground(
                guiGraphics, TextureLocations.UI_BACKGROUND_SLIM,
                this.guiLeft, this.guiTop,
                this.xSize, this.ySize
        );

        guiGraphics.drawCenteredString(
                minecraft.font,
                Component.translatable("gui.glowingeyes.warning").withStyle(ChatFormatting.BOLD),
                middleX, this.guiTop + 7,
                Color.RED.getRGB()
        );

        GuiUtil.drawWrappedText(
                guiGraphics,
                minecraft.font,
                Component.translatable("gui.glowingeyes.reset.title"),
                this.guiLeft + (xSize / 2), middleY - 20,
                this.xSize - 14,
                Color.WHITE.getRGB()
        );

        GuiUtil.drawWrappedText(
                guiGraphics,
                minecraft.font,
                Component.translatable("gui.glowingeyes.reset.warning"),
                this.guiLeft + (xSize / 2), middleY + 5,
                this.xSize - 14,
                Color.RED.getRGB()
        );

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
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
