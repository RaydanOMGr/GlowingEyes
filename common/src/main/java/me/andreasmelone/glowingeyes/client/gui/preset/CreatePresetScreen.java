package me.andreasmelone.glowingeyes.client.gui.preset;

import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CreatePresetScreen extends Screen {
    private static final int UI_WIDTH = TextureLocations.UI_BACKGROUND_SLIM_WIDTH;
    private static final int UI_HEIGHT = TextureLocations.UI_BACKGROUND_SLIM_HEIGHT;

    private static final int TEXT_Y = 10;
    private static final int NAME_FIELD_Y = 45;
    private static final int NAME_FIELD_HEIGHT = 20;

    private static final int PADDING_X = 20;
    private static final int BUTTON_SPACING = 5;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_Y = UI_HEIGHT - BUTTON_HEIGHT - 20;

    private int guiLeft;
    private int guiTop;

    private EditBox nameField;
    private final Screen parent;
    public CreatePresetScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();
        if (this.parent != null) {
            this.parent.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            this.parent.clearFocus();
        }
        this.guiLeft = (this.width - UI_WIDTH) / 2;
        this.guiTop = (this.height - UI_HEIGHT) / 2;

        this.addRenderableWidget(this.nameField =
                new EditBox(this.font,
                        this.guiLeft + PADDING_X, this.guiTop + NAME_FIELD_Y,
                        UI_WIDTH - (PADDING_X * 2), NAME_FIELD_HEIGHT,
                        Component.empty()
                ));

        int buttonWidth = UI_WIDTH / 2 - BUTTON_SPACING - PADDING_X;
        this.addRenderableWidget(
                Button.builder(
                                Component.translatable("gui.glowingeyes.create"),
                                button -> {
                                    PresetManager.getInstance().createPreset(this.nameField.getValue(), GlowingEyesComponent.getGlowingEyesMap(Minecraft.getInstance().player));
                                    Minecraft.getInstance().setScreen(this.parent);
                                }
                        ).pos(this.guiLeft + PADDING_X, this.guiTop + BUTTON_Y)
                        .size(buttonWidth, BUTTON_HEIGHT)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(
                                Component.translatable("gui.glowingeyes.cancel"),
                                button -> {
                                    Minecraft.getInstance().setScreen(this.parent);
                                }
                        ).pos(this.guiLeft + PADDING_X + (BUTTON_SPACING * 2) + buttonWidth, this.guiTop + BUTTON_Y)
                        .size(buttonWidth, BUTTON_HEIGHT)
                        .build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        if (this.parent != null) {
            ctx.pose().pushPose();
            ctx.pose().translate(0, 0, -100);
            this.parent.render(ctx, 0, 0, partialTicks);
            ctx.pose().popPose();
            GuiUtil.drawTransparentBlack(ctx);
        } else super.renderBackground(ctx, mouseX, mouseY, partialTicks);
        GuiUtil.drawBackground(ctx, TextureLocations.UI_BACKGROUND_SLIM, this.guiLeft, this.guiTop, UI_WIDTH, UI_HEIGHT);

        ctx.drawCenteredString(this.font, Component.translatable("gui.glowingeyes.create.title"), this.width / 2, this.guiTop + TEXT_Y, Color.WHITE.getRGB());

        super.render(ctx, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        if (this.parent != null) {
            Minecraft.getInstance().setScreen(this.parent);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
    }
}