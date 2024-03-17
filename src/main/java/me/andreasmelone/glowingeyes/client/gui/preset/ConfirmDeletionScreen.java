package me.andreasmelone.glowingeyes.client.gui.preset;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;

import java.util.List;
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
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.middleX = this.guiLeft + this.xSize / 2;
        this.middleY = this.guiTop + this.ySize / 2;

        labelComponent = Component.empty();

        this.addRenderableWidget(new Button(
                this.guiLeft + 20, this.guiTop + 100,
                80 - 5, 20,
                Component.translatable("gui.confirm"),
                button -> {
                    if(this.future != null) {
                        this.future.complete(true);
                    }
                    if(this.parent != null) {
                        Minecraft.getInstance().setScreen(this.parent);
                    }
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + 100 + (5 * 2), this.guiTop + 100,
                80 - 5, 20,
                Component.translatable("gui.cancel"),
                button -> {
                    if(this.future != null) {
                        this.future.complete(false);
                    }
                    if(this.parent != null) {
                        Minecraft.getInstance().setScreen(this.parent);
                    }
                }
        ));
        labelComponent = Component.translatable("gui.delete.confirm", this.deletedElement);
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
                poseStack,
                this.font,
                labelComponent,
                this.middleX, this.middleY - 20,
                0xFFFFFF
        );

        super.render(poseStack, mouseX, mouseY, partialTicks);
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
