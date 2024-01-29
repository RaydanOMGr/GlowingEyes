package me.andreasmelone.glowingeyes.client.ui.preset;

import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConfirmDeletionScreen extends GuiScreen {
    private final GuiScreen parent;
    private String deletedElement;
    private CompletableFuture<Boolean> future;

    public ConfirmDeletionScreen(GuiScreen parent) {
        this.parent = parent;
    }

    int xSize = 200;
    int ySize = 143;

    int guiLeft;
    int guiTop;

    GuiLabel label;

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.buttonList.add(new GuiButton(
                0,
                this.guiLeft + 20, this.guiTop + 100,
                80 - 5, 20,
                I18n.format("gui.confirm")
        ));
        this.buttonList.add(new GuiButton(
                1,
                this.guiLeft + 100 + (5 * 2), this.guiTop + 100,
                80 - 5, 20,
                I18n.format("gui.cancel")
        ));

        this.labelList.add(
                this.label = new GuiLabel(
                        this.fontRenderer,
                        0,
                        this.guiLeft + 20, this.guiTop + 50,
                        this.xSize - (20 * 2), 20,
                        0xFFFFFF
                )
        );
        String formated = I18n.format("gui.delete.confirm", this.deletedElement);

        this.label.setCentered();
        // split the text up so it fits on the screen
        List<String> lines = this.fontRenderer.listFormattedStringToWidth(formated, this.xSize - (20 * 2));
        for (String line : lines) {
            this.label.addLine(line);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        parent.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();

        GuiUtil.drawBackground(TextureLocations.UI_BACKGROUND_SLIM, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        this.label.drawLabel(mc, mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 0) {
            future.complete(true);
        } else if(button.id == 1) {
            future.complete(false);
        }
        this.mc.displayGuiScreen(this.parent);
    }

    public static CompletableFuture<Boolean> askToDelete(GuiScreen parent, String element) {
        ConfirmDeletionScreen screen = new ConfirmDeletionScreen(parent);
        screen.deletedElement = element;
        Minecraft.getMinecraft().displayGuiScreen(screen);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        screen.future = future;
        return future;
    }
}
