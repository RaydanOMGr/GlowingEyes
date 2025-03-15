package me.andreasmelone.glowingeyes.client.gui.preset;

import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

public class EditPresetScreen extends Screen {
    private int guiTop, guiLeft;

    private String elementName;
    private CompletableFuture<String> future;

    EditBox nameField;
    Screen parent;

    private final int xSize = 200;
    private final int ySize = 143;
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
        super.init();
        if(parent != null) parent.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addRenderableWidget(nameField = new EditBox(
                this.font,
                this.guiLeft + 20, this.guiTop + 50,
                this.xSize - (20 * 2), 20,
                Component.literal(this.elementName)
        ));
        nameField.setFocused(true);

        // make an "apply" and a cancel button
        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.glowingeyes.apply"),
                button -> {
                    if (parent != null) {
                        future.complete(nameField.getValue());
                        Minecraft.getInstance().setScreen(parent);
                    }
                }
            ).pos(this.guiLeft + 20, this.guiTop + 100)
            .size(80 - 5, 20)
            .build()
        );

        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.glowingeyes.cancel"),
                button -> {
                    if (parent != null) {
                        Minecraft.getInstance().setScreen(parent);
                    }
                }
            ).pos(this.guiLeft + 100 + (5 * 2), this.guiTop + 100)
            .size(80 - 5, 20)
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

        GuiUtil.drawWrappedText(
                guiGraphics,
                this.font,
                Component.translatable("gui.glowingeyes.edit.title"),
                this.width / 2, this.guiTop + 10,
                this.xSize - 14,
                0xFFFFFF
        );

//        drawCenteredString(
//                poseStack, this.font,
//                Component.translatable("gui.name"),
//                this.width / 2, this.guiTop + 40,
//                0xFFFFFF
//        );

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public static CompletableFuture<String> askForName(Screen parent, String elementName) {
        EditPresetScreen screen = new EditPresetScreen(parent);
        screen.elementName = elementName;
        Minecraft.getInstance().setScreen(screen);
        CompletableFuture<String> future = new CompletableFuture<>();
        screen.future = future;
        return future;
    }
}
