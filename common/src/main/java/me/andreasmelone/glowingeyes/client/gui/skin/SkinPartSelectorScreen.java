package me.andreasmelone.glowingeyes.client.gui.skin;

import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class SkinPartSelectorScreen extends Screen {
    private int paddingX;
    private int paddingY;
    private int maxTextureWidth;
    private int maxTextureHeight;
    private int xCenter;
    private int yCenter;
    private int guiLeft, guiTop;
    private int middle;
    private float factorX;
    private float factorY;
    private SkinPart selected = null;
    private CompletableFuture<SkinPart> future;

    private final int rows = 2;
    private final int xSize = 200;
    private final int ySize = 143;
    private final Screen parent;
    private final ResourceLocation skinTexture;
    private final Color overlayColor = new Color(255, 255, 255, 120);

    protected SkinPartSelectorScreen(Screen parent, ResourceLocation skinTexture, SkinPart selected) {
        super(Component.empty());
        this.parent = parent;
        this.skinTexture = skinTexture;
        this.selected = selected;
    }

    @Override
    protected void init() {
        super.init();
        if (parent != null)
            parent.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        paddingX = 5;
        paddingY = 5;

        int paddedWidth = xSize - 2 * paddingX;
        int paddedHeight = ySize - 2 * paddingY;

        float scaleX = (float) paddedWidth / 64.0f;
        float scaleY = (float) paddedHeight / SkinPart.getRowY(rows);
        float scaleFactor = Math.min(scaleX, scaleY);

        maxTextureWidth = (int) (64 * scaleFactor);
        maxTextureHeight = (int) (SkinPart.getRowY(rows) * scaleFactor);

        xCenter = this.guiLeft + (xSize - maxTextureWidth) / 2;
        yCenter = this.guiTop + (ySize - maxTextureHeight) / 2;

        factorX = 64.0f / maxTextureWidth;
        factorY = (float) SkinPart.getRowY(rows) / maxTextureHeight;

        middle = this.guiLeft + (xSize / 2);

        int width = (middle - guiLeft) - 7;
        int doneX = guiLeft + 5;
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.done"), (button) -> {
                    if (selected == null) return;
                    this.future.complete(selected);
                    this.minecraft.setScreen(parent);
                }).pos(doneX, this.guiTop + this.ySize - 25).size(width, 20).build()
        );

        int cancelX = middle + 2;
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.cancel"), (button) -> {
                    this.future.complete(null);
                    this.minecraft.setScreen(parent);
                }).pos(cancelX, this.guiTop + this.ySize - 25).size(width, 20).build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(parent != null) parent.render(guiGraphics, 0, 0, partialTicks);
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        GuiUtil.drawBackground(
                guiGraphics, TextureLocations.UI_BACKGROUND_SLIM,
                this.guiLeft, this.guiTop,
                this.xSize, this.ySize
        );

        guiGraphics.drawCenteredString(
                minecraft.font,
                Component.translatable("gui.glowingeyes.skinpartselection"),
                middle, guiTop + 7,
                Color.WHITE.getRGB()
        );

        guiGraphics.blit(
                skinTexture,
                xCenter, yCenter,
                maxTextureWidth, maxTextureHeight,
                0, 0,
                64, SkinPart.getRowY(rows),
                64, 64
        );

        int textureMouseX = (int) ((mouseX - xCenter) * factorX);
        int textureMouseY = (int) ((mouseY - yCenter) * factorY);

        if (textureMouseX >= 0 && textureMouseX <= 63 && textureMouseY >= 0 && textureMouseY <= 63) {
            SkinPart part = SkinPart.getFromCoordinates(textureMouseX, textureMouseY);
            if (part != null && part.containsData() && part.getRow() < rows) {
                int x = part.getX();
                int y = part.getY();

                guiGraphics.fill(
                        xCenter + (int) (x / factorX),
                        yCenter + (int) (y / factorY),
                        xCenter + (int) ((x + part.getSizeX()) / factorX),
                        yCenter + (int) ((y + part.getSizeY()) / factorY),
                        overlayColor.getRGB()
                );

                guiGraphics.renderTooltip(
                        minecraft.font,
                        Component.translatable(part.getTranslationKey()),
                        mouseX, mouseY
                );
            }
        }

        if (selected != null) {
            guiGraphics.fill(
                    xCenter + (int) (selected.getX() / factorX),
                    yCenter + (int) (selected.getY() / factorY),
                    xCenter + (int) ((selected.getX() + selected.getSizeX()) / factorX),
                    yCenter + (int) ((selected.getY() + selected.getSizeY()) / factorY),
                    overlayColor.getRGB()
            );
            guiGraphics.renderOutline(
                    xCenter + (int) (selected.getX() / factorX),
                    yCenter + (int) (selected.getY() / factorY),
                    (int) (selected.getSizeX() / factorY) + 1,
                    (int) (selected.getSizeY() / factorX) + 1,
                    Color.BLACK.getRGB()
            );
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int textureMouseX = (int) ((mouseX - xCenter) * factorX);
        int textureMouseY = (int) ((mouseY - yCenter) * factorY);
        SkinPart part = SkinPart.getFromCoordinates(textureMouseX, textureMouseY);
        if (part != null && part.containsData() && part.getRow() < rows) {
            if (button == 0) selected = part;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    public void onClose() {
        super.onClose();
        if (parent != null) {
            Minecraft.getInstance().setScreen(parent);
            parent.init(
                    Minecraft.getInstance(),
                    Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                    Minecraft.getInstance().getWindow().getGuiScaledHeight()
            );
        }
    }

    public static CompletableFuture<SkinPart> create(Screen parent, ResourceLocation skin, SkinPart selected) {
        CompletableFuture<SkinPart> future = new CompletableFuture<>();
        SkinPartSelectorScreen screen = new SkinPartSelectorScreen(parent, skin, selected);
        screen.future = future;
        Minecraft.getInstance().setScreen(screen);
        return future;
    }

    public static CompletableFuture<SkinPart> create(Screen parent, ResourceLocation skin) {
        return create(parent, skin, SkinPart.HEAD_FRONT);
    }
}
