package me.andreasmelone.glowingeyes.client.gui.skin;

import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.SkinUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SkinPartSelectorScreen extends Screen {
    private int paddingX;
    private int paddingY;
    private int maxTextureWidth;
    private int maxTextureHeight;
    private float xCenter;
    private float yCenter;
    private int guiLeft, guiTop;
    private int middle;
    private float factorX;
    private float factorY;
    private ISkinPart selected = null;
    private CompletableFuture<ISkinPart> future;

    private final int rows = 7;
    private final int xSize = 221;
    private final int ySize = 222;
    private final Screen parent;
    private final ResourceLocation skinTexture;
    private final Color overlayColor = new Color(255, 255, 255, 120);

    protected SkinPartSelectorScreen(Screen parent, ResourceLocation skinTexture, @NotNull ISkinPart selected) {
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
        float scaleY = (float) paddedHeight / ISkinPart.getRowY(rows, selected.isSlim());
        float scaleFactor = Math.min(scaleX, scaleY);

        maxTextureWidth = (int) (64 * scaleFactor);
        maxTextureHeight = (int) (ISkinPart.getRowY(rows, selected.isSlim()) * scaleFactor);

        xCenter = this.guiLeft + (float) (xSize - maxTextureWidth) / 2;
        yCenter = this.guiTop + (float) (ySize - maxTextureHeight) / 2 - 3;

        factorX = 64.0f / maxTextureWidth;
        factorY = (float) ISkinPart.getRowY(rows, selected.isSlim()) / maxTextureHeight;

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
        if (parent != null) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, -10000);
            parent.render(guiGraphics, 0, 0, partialTicks);
            guiGraphics.pose().popPose();
        }
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        GuiUtil.drawBackground(
                guiGraphics, TextureLocations.UI_BACKGROUND_BIG,
                this.guiLeft, this.guiTop,
                this.xSize, this.ySize
        );

        guiGraphics.drawCenteredString(
                minecraft.font,
                Component.translatable("gui.glowingeyes.skinpartselection"),
                middle, guiTop + 7,
                Color.WHITE.getRGB()
        );

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(xCenter, yCenter, 0);
        guiGraphics.blit(
                RenderType::guiTextured,
                skinTexture,
                0, 0,
                0, 0,
                maxTextureWidth, maxTextureHeight,
                64, ISkinPart.getRowY(rows, selected.isSlim()),
                64, 64
        );
        guiGraphics.pose().popPose();

        int textureMouseX = (int) ((mouseX - xCenter) * factorX);
        int textureMouseY = (int) ((mouseY - yCenter) * factorY);

        if (textureMouseX >= 0 && textureMouseX <= 63 && textureMouseY >= 0 && textureMouseY <= 63) {
            ISkinPart part = ISkinPart.getFromCoordinates(textureMouseX, textureMouseY, selected.isSlim());
            if (part != null && part.containsData() && part.getRow() < rows) {
                int x = part.getX();
                int y = part.getY();

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(xCenter, yCenter, 0);
                guiGraphics.pose().scale(1.0f / factorX, 1.0f / factorY, 1.0f);
                guiGraphics.fill(
                        x,
                        y,
                        (x + part.getSizeX()),
                        (y + part.getSizeY()),
                        overlayColor.getRGB()
                );
                guiGraphics.pose().popPose();

                guiGraphics.renderTooltip(
                        minecraft.font,
                        Component.translatable(part.getTranslationKey()),
                        mouseX, mouseY
                );
            }
        }

        if (selected != null) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(xCenter + (selected.getX() / factorX) - 0.25, yCenter + (selected.getY() / factorY) - 0.25, 0);
            guiGraphics.fill(
                    0,
                    0,
                    (int) ((selected.getSizeX()) / factorX),
                    (int) ((selected.getSizeY()) / factorY),
                    overlayColor.getRGB()
            );
            guiGraphics.renderOutline(
                    0,
                    0,
                    (int) (selected.getSizeX() / factorY) + 1,
                    (int) (selected.getSizeY() / factorX) + 1,
                    Color.BLACK.getRGB()
            );
            guiGraphics.pose().popPose();
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int textureMouseX = (int) ((mouseX - xCenter) * factorX);
        int textureMouseY = (int) ((mouseY - yCenter) * factorY);
        ISkinPart part = ISkinPart.getFromCoordinates(textureMouseX, textureMouseY, selected.isSlim());
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
        }
    }

    public static CompletableFuture<ISkinPart> create(Screen parent, ResourceLocation skin, ISkinPart selected) {
        CompletableFuture<ISkinPart> future = new CompletableFuture<>();
        SkinPartSelectorScreen screen = new SkinPartSelectorScreen(parent, skin, selected);
        screen.future = future;
        Minecraft.getInstance().setScreen(screen);
        return future;
    }

    public static CompletableFuture<ISkinPart> create(Screen parent, ResourceLocation skin) {
        return create(parent, skin, SkinUtil.isSlim() ? SlimSkinPart.HEAD_FRONT : ClassicSkinPart.HEAD_FRONT);
    }
}
