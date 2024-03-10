package me.andreasmelone.glowingeyes.client.gui.preset;

import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

public class EditPresetScreen extends Screen {
    private int xSize = 200;
    private int ySize = 143;

    private int guiTop, guiLeft;

    private String elementName;
    private CompletableFuture<String> future;

    EditBox nameField;
    Screen parent;

    protected EditPresetScreen() {
        super(Component.empty());
        this.parent = null;
    }

    protected EditPresetScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addRenderableWidget(nameField = new EditBox(
                this.font,
                this.guiLeft + 20, this.guiTop + 50,
                this.xSize - (20 * 2), 20,
                Component.literal(this.elementName)
        ));
        nameField.setFocus(true);

        // make an "apply" and a cancel button
        this.addRenderableWidget(new Button(
                0, this.guiLeft + 20,
                this.guiTop + 100, 80 - 5,
                Component.translatable("gui.apply"),
                button -> {
                    if(parent != null) {
                        this.getMinecraft().setScreen(parent);
                    }
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + 100 + (5 * 2), this.guiTop + 100,
                80 - 5, 20,
                Component.translatable("gui.cancel"),
                button -> {
                    if(parent != null) {
                        this.getMinecraft().setScreen(parent);
                    }
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
                Component.translatable("gui.edit.title"),
                this.width / 2, this.guiTop + 10,
                0xFFFFFF
        );

        drawCenteredString(
                poseStack, this.font,
                Component.translatable("gui.name"),
                this.width / 2, this.guiTop + 40,
                0xFFFFFF
        );

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    public static CompletableFuture<String> askForName(Screen parent, String elementName) {
        EditPresetScreen screen = new EditPresetScreen(parent);
        screen.elementName = elementName;
        screen.getMinecraft().setScreen(screen);
        CompletableFuture<String> future = new CompletableFuture<>();
        screen.future = future;
        return future;
    }
}
