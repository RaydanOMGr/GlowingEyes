package me.andreasmelone.glowingeyes.client.gui.preset;

import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ConfirmDeletionScreen extends Screen {
    private static final int UI_WIDTH = TextureLocations.UI_BACKGROUND_SLIM_WIDTH;
    private static final int UI_HEIGHT = TextureLocations.UI_BACKGROUND_SLIM_HEIGHT;

    private static final int TEXT_Y = 20;
    private static final int TEXT_PADDING = 7;

    private static final int PADDING_X = 20;
    private static final int BUTTON_SPACING = 5;

    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_Y = UI_HEIGHT - BUTTON_HEIGHT - 20;

    int guiLeft, guiTop;

    private String deletedElement;
    private Component labelComponent;
    private CompletableFuture<Boolean> future;
    private final Screen parent;
    public ConfirmDeletionScreen() {
        super(Component.empty());
        this.parent = null;
    }

    public ConfirmDeletionScreen(Screen parent) {
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

        int buttonWidth = UI_WIDTH / 2 - BUTTON_SPACING - PADDING_X;

        // First Button: Confirm
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.glowingeyes.confirm"),
                                button -> {
                                    if (this.future != null) {
                                        this.future.complete(true);
                                    }
                                    if (this.parent != null) {
                                        Minecraft.getInstance().setScreen(this.parent);
                                    }
                                })
                        .pos(this.guiLeft + PADDING_X, this.guiTop + BUTTON_Y)
                        .size(buttonWidth, BUTTON_HEIGHT)
                        .build()
        );

        // Second Button: Cancel
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.glowingeyes.cancel"),
                                button -> {
                                    if (this.future != null) {
                                        this.future.complete(false);
                                    }
                                    if (this.parent != null) {
                                        Minecraft.getInstance().setScreen(this.parent);
                                    }
                                })
                        .pos(this.guiLeft + PADDING_X + (BUTTON_SPACING * 2) + buttonWidth, this.guiTop + BUTTON_Y)
                        .size(buttonWidth, BUTTON_HEIGHT)
                        .build()
        );

        this.labelComponent = Component.translatable("gui.glowingeyes.delete.confirm", this.deletedElement);
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        if (this.parent != null) {
            ctx.pose().pushPose();
            ctx.pose().translate(0, 0, -100);
            this.parent.render(ctx, 0, 0, partialTicks);
            ctx.pose().popPose();
        }
        super.renderBackground(ctx, mouseX, mouseY, partialTicks);

        GuiUtil.drawBackground(
                ctx, TextureLocations.UI_BACKGROUND_SLIM,
                this.guiLeft, this.guiTop,
                UI_WIDTH, UI_HEIGHT
        );

        GuiUtil.drawWrappedText(
                ctx,
                this.font,
                this.labelComponent,
                this.width / 2, (this.height / 2) - TEXT_Y,
                UI_WIDTH - TEXT_PADDING * 2,
                0xFFFFFF
        );

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

    public static CompletableFuture<Boolean> askToDelete(Screen parent, String element) {
        ConfirmDeletionScreen screen = new ConfirmDeletionScreen(parent);
        screen.deletedElement = element;
        Minecraft.getInstance().setScreen(screen);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        screen.future = future;
        return future;
    }
}
