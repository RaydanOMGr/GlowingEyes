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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ConfirmResetScreen extends Screen {
    private static final int UI_WIDTH = TextureLocations.UI_BACKGROUND_SLIM_WIDTH;
    private static final int UI_HEIGHT = TextureLocations.UI_BACKGROUND_SLIM_HEIGHT;

    private static final int BUTTON_Y = UI_HEIGHT - 25;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_OFFSET_MIDDLE_X = 2;

    private static final int TEXT_PADDING = 14;
    private static final int TITLE_Y = -20;
    private static final int WARNING_Y = 5;

    private int guiLeft;
    private int guiTop;
    private int middleX;
    private int middleY;
    private final Screen parent;
    protected ConfirmResetScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        if(this.parent != null) {
            this.parent.init(this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            this.parent.clearFocus();
        }
        this.guiLeft = (this.width - UI_WIDTH) / 2;
        this.guiTop = (this.height - UI_HEIGHT) / 2;

        this.middleX = this.guiLeft + (UI_WIDTH / 2);
        this.middleY = this.guiTop + (UI_HEIGHT / 2);

        int width = this.middleX - this.guiLeft - TEXT_PADDING / 2;
        int confirmX = this.middleX - width - BUTTON_OFFSET_MIDDLE_X;
        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.glowingeyes.confirm"),
                button -> {
                    if (this.parent != null) {
                        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, new HashMap<>());
                        ClientGlowingEyesComponent.sendUpdate();
                        Minecraft.getInstance().setScreen(this.parent);
                    }
                }
            ).pos(confirmX, this.guiTop + BUTTON_Y)
            .size(width, BUTTON_HEIGHT)
            .build()
        );

        int cancelX = this.middleX + BUTTON_OFFSET_MIDDLE_X;
        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.glowingeyes.cancel"),
                button -> {
                    if (this.parent != null) {
                        Minecraft.getInstance().setScreen(this.parent);
                    }
                }
            ).pos(cancelX, this.guiTop + BUTTON_Y)
            .size(width, BUTTON_HEIGHT)
            .build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        if(this.parent != null) {
            this.parent.render(ctx, 0, 0, partialTicks);
            GuiUtil.drawTransparentBlack(ctx);
        } else super.renderBackground(ctx, mouseX, mouseY, partialTicks);
        GuiUtil.drawBackground(
                ctx, TextureLocations.UI_BACKGROUND_SLIM,
                this.guiLeft, this.guiTop,
                UI_WIDTH, UI_HEIGHT
        );

        ctx.drawCenteredString(
                this.minecraft.font,
                Component.translatable("gui.glowingeyes.warning").withStyle(ChatFormatting.BOLD),
                this.middleX, this.guiTop + TEXT_PADDING / 2,
                Color.RED.getRGB()
        );

        GuiUtil.drawWrappedText(
                ctx,
                this.minecraft.font,
                Component.translatable("gui.glowingeyes.reset.title"),
                this.guiLeft + (UI_WIDTH / 2), this.middleY + TITLE_Y,
                UI_WIDTH - TEXT_PADDING,
                Color.WHITE.getRGB()
        );

        GuiUtil.drawWrappedText(
                ctx,
                this.minecraft.font,
                Component.translatable("gui.glowingeyes.reset.warning"),
                this.guiLeft + (UI_WIDTH / 2), this.middleY + WARNING_Y,
                UI_WIDTH - TEXT_PADDING,
                Color.RED.getRGB()
        );

        super.render(ctx, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        if(this.parent != null) {
            Minecraft.getInstance().setScreen(this.parent);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
    }
}
