package me.andreasmelone.glowingeyes.client.gui.preset;

import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

public class ConfirmDeletionScreen extends Screen {
    int xSize = 200;
    int ySize = 143;

    int guiLeft, guiTop, middleX, middleY;

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
        if(parent != null) parent.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.middleX = this.guiLeft + this.xSize / 2;
        this.middleY = this.guiTop + this.ySize / 2;

        labelComponent = Component.empty();

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
                        .pos(this.guiLeft + 20, this.guiTop + 100)
                        .size(80 - 5, 20)
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
                        .pos(this.guiLeft + 100 + (5 * 2), this.guiTop + 100)
                        .size(80 - 5, 20)
                        .build()
        );

        labelComponent = Component.translatable("gui.glowingeyes.delete.confirm", this.deletedElement);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(parent != null) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, -100);
            parent.render(guiGraphics, 0, 0, partialTicks);
            guiGraphics.pose().popPose();
        }
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

        GuiUtil.drawBackground(
                guiGraphics, TextureLocations.UI_BACKGROUND_SLIM,
                this.guiLeft, this.guiTop,
                this.xSize, this.ySize
        );

        GuiUtil.drawWrappedText(
                guiGraphics,
                this.font,
                labelComponent,
                this.middleX, this.middleY - 20,
                this.xSize - 14,
                0xFFFFFF
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

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
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
